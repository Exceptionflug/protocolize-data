package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 27.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class ContainerClick extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x07),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x08),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x07),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x08),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_16_4, 0x09),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x08),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19, 0x0A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_19_2, 0x0B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_1_19_3, 0x0A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_4, MINECRAFT_1_20_1, 0x0B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_1_20_4, 0x0D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_21, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_2, MINECRAFT_LATEST, 0x10)
    );

    private Map<Short, ItemStack> slotData = new HashMap<>();
    private int windowId;
    private int actionNumber;
    private short slot;
    private byte button;
    private int mode;
    private ItemStack itemStack;

    /**
     * @since 1.7.1-SNAPSHOT protocol 756
     */
    private int stateId;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        this.windowId = (protocolVersion >= MINECRAFT_1_21_2) ? ProtocolUtil.readVarInt(buf) : buf.readUnsignedByte();
        if (protocolVersion >= MINECRAFT_1_17_1) {
            this.stateId = ProtocolUtil.readVarInt(buf);
        }
        this.slot = buf.readShort();
        this.button = buf.readByte();
        if (protocolVersion < MINECRAFT_1_17) {
            this.actionNumber = buf.readShort();
        }
        if (protocolVersion == MINECRAFT_1_8) {
            this.mode = buf.readByte();
        } else {
            this.mode = ProtocolUtil.readVarInt(buf);
        }
        if (protocolVersion >= MINECRAFT_1_17) {
            int length = ProtocolUtil.readVarInt(buf);
            for (int i = 0; i < length; i++) {
                this.slotData.put(buf.readShort(), ItemStackSerializer.read(buf, protocolVersion));
            }
        }
        this.itemStack = ItemStackSerializer.read(buf, protocolVersion);
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if(protocolVersion >= MINECRAFT_1_21_2) {
            ProtocolUtil.writeVarInt(buf, this.windowId);
        } else {
            buf.writeByte(this.windowId & 0xFF);
        }
        if (protocolVersion >= MINECRAFT_1_17_1) {
            ProtocolUtil.writeVarInt(buf, this.stateId);
        }
        buf.writeShort(this.slot);
        buf.writeByte(this.button);
        if (protocolVersion < MINECRAFT_1_17) {
            buf.writeShort(this.actionNumber);
        }
        if (protocolVersion == MINECRAFT_1_8) {
            buf.writeByte(this.mode);
        } else {
            ProtocolUtil.writeVarInt(buf, this.mode);
        }
        if (protocolVersion >= MINECRAFT_1_17) {
            ProtocolUtil.writeVarInt(buf, this.slotData.size());
            for (short slot : this.slotData.keySet()) {
                buf.writeShort(slot);
                ItemStackSerializer.write(buf, this.slotData.get(slot), protocolVersion);
            }
        }
        if (this.itemStack == null) {
            ItemStackSerializer.write(buf, ItemStack.NO_DATA, protocolVersion);
        } else {
            ItemStackSerializer.write(buf, this.itemStack, protocolVersion);
        }
    }

    public ClickType clickType() {
        return ClickType.getType(this.mode, this.button);
    }

    public void clickType(ClickType clickType) {
        this.mode = clickType.mode();
        this.button = (byte) clickType.button();
    }

}
