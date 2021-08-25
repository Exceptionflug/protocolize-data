package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.item.ItemStack;
import dev.simplix.protocolize.data.item.ItemStackSerializer;
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
@AllArgsConstructor
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
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_17_1, 0x14)
    );

    private short windowId;
    private List<ItemStack> items = new ArrayList<>();

    /**
     * @since Protocol 756
     */
    private ItemStack cursorItem = ItemStack.NO_DATA;

    /**
     * @since Protocol 756
     */
    private int stateId;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        windowId = buf.readUnsignedByte();
        int count;
        if (protocolVersion >= MINECRAFT_1_17_1) {
            stateId = ProtocolUtil.readVarInt(buf);
            count = ProtocolUtil.readVarInt(buf);
        } else {
            count = buf.readShort();
        }
        for (int i = 0; i < count; i++) {
            items.add(ItemStackSerializer.read(buf, protocolVersion));
        }
        if (protocolVersion >= MINECRAFT_1_17_1) {
            cursorItem = ItemStackSerializer.read(buf, protocolVersion);
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        buf.writeByte(windowId & 0xFF);
        if (protocolVersion >= MINECRAFT_1_17_1) {
            ProtocolUtil.writeVarInt(buf, stateId);
            ProtocolUtil.writeVarInt(buf, items.size());
        } else {
            buf.writeShort(items.size());
        }
        for (ItemStack item : items) {
            if (item == null)
                item = ItemStack.NO_DATA;
            ItemStackSerializer.write(buf, item, protocolVersion);
        }
        if (protocolVersion >= MINECRAFT_1_17_1) {
            ItemStackSerializer.write(buf, cursorItem, protocolVersion);
        }
    }

}
