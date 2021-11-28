package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.LazyBuffer;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 25.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class WindowItems extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x30),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x15),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x15),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_LATEST, 0x14)
    );

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private LazyBuffer lazyBuffer = LazyBuffer.empty();

    private short windowId;
    private List<ItemStack> items = new ArrayList<>();
    private int count;

    /**
     * @since Protocol 756
     */
    private ItemStack cursorItem = ItemStack.NO_DATA;

    /**
     * @since Protocol 756
     */
    private int stateId;

    public WindowItems(short windowId, List<ItemStack> items, int stateId) {
        this.windowId = windowId;
        this.items = items;
        this.count = items.size();
        this.stateId = stateId;
    }

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        windowId = buf.readUnsignedByte();
        if (protocolVersion >= MINECRAFT_1_17_1) {
            stateId = ProtocolUtil.readVarInt(buf);
            count = ProtocolUtil.readVarInt(buf);
        } else {
            count = buf.readShort();
        }

        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        lazyBuffer = new LazyBuffer(data, (wrappedBuf) -> {
            for (int i = 0; i < count; i++) {
                items.add(ItemStackSerializer.read(wrappedBuf, protocolVersion));
            }
            if (protocolVersion >= MINECRAFT_1_17_1) {
                cursorItem = ItemStackSerializer.read(wrappedBuf, protocolVersion);
            }
        });
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        buf.writeByte(windowId & 0xFF);
        if (protocolVersion >= MINECRAFT_1_17_1) {
            ProtocolUtil.writeVarInt(buf, stateId);
            ProtocolUtil.writeVarInt(buf, count);
        } else {
            buf.writeShort(count);
        }
        lazyBuffer.write(buf, () -> {
            for (ItemStack item : items) {
                if (item == null)
                    item = ItemStack.NO_DATA;
                ItemStackSerializer.write(buf, item, protocolVersion);
            }
            if (protocolVersion >= MINECRAFT_1_17_1) {
                ItemStackSerializer.write(buf, cursorItem, protocolVersion);
            }
        });
    }

    public List<ItemStack> items() {
        lazyBuffer.read();
        return items;
    }

    public void items(List<ItemStack> items) {
        this.lazyBuffer = LazyBuffer.empty();
        this.items = items;
        this.count = items.size();
    }

    /**
     * @since Protocol 756
     */
    public ItemStack cursorItem() {
        lazyBuffer.read();
        return cursorItem;
    }

}
