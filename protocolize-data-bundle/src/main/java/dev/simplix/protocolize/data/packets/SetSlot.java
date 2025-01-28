package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.item.component.exception.InvalidDataComponentTypeException;
import dev.simplix.protocolize.api.item.component.exception.InvalidDataComponentVersionException;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.DebugUtil;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.LazyBuffer;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 25.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class SetSlot extends AbstractPacket {

    /* ClientboundContainerSetSlotPacket */

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x2F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x16),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x17),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x16),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x17),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x16),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x15),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x16),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19_2, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_1_19_3, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_4, MINECRAFT_1_20_1, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_LATEST, 0x15)
    );

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private LazyBuffer lazyBuffer = LazyBuffer.empty();

    private int windowId;
    private short slot;
    private BaseItemStack itemStack = ItemStack.NO_DATA;

    /**
     * @since Protocol 756
     */
    private int stateId;

    // skip decoding and save raw packet data
    boolean skipDecoding = false;
    byte[] packetData = null;

    public SetSlot(byte windowId, short slot, BaseItemStack itemStack,
                   int stateId) {
        this.windowId = windowId;
        this.slot = slot;
        this.itemStack = itemStack;
        this.stateId = stateId;
    }

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        readEntirePacket(buf);
        StringBuilder sb = new StringBuilder();
        sb.append("SetSlot:");
        try {
            this.windowId = (protocolVersion >= MINECRAFT_1_21_2) ? ProtocolUtil.readVarInt(buf) : buf.readByte();
            sb.append("\n    Window ID: 0x").append(Integer.toHexString(this.windowId));
            if (protocolVersion >= MINECRAFT_1_17_1) {
                this.stateId = ProtocolUtil.readVarInt(buf);
                sb.append("\n    State ID: 0x").append(Integer.toHexString(this.stateId));
            }
            this.slot = buf.readShort();
            sb.append("\n    Slot: 0x").append(Integer.toHexString(this.slot));

            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            this.lazyBuffer = new LazyBuffer(data, byteBuf -> {
                this.itemStack = ItemStackSerializer.read(byteBuf, protocolVersion);
            });
        } catch (Exception e) {
            if(DebugUtil.enabled) log.info(sb.toString());
            if((e instanceof InvalidDataComponentVersionException || e instanceof InvalidDataComponentTypeException) ||
                (e.getCause() != null && (e.getCause() instanceof InvalidDataComponentVersionException || e.getCause() instanceof InvalidDataComponentTypeException))){
                log.error("Skipping decoding SetSlot packet: {}", e.getMessage());
            } else {
                log.error("Skipping decoding SetSlot packet", e);
            }
            skipDecoding = true;
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if(packetData != null && skipDecoding){
            buf.writeBytes(packetData);
            return;
        }

        if(protocolVersion >= MINECRAFT_1_21_2) {
            ProtocolUtil.writeVarInt(buf, this.windowId);
        } else {
            buf.writeByte(this.windowId & 0xFF);
        }
        if (protocolVersion >= MINECRAFT_1_17_1) {
            ProtocolUtil.writeVarInt(buf, this.stateId);
        }
        buf.writeShort(this.slot);
        this.lazyBuffer.write(buf, () -> {
            ItemStackSerializer.write(buf, this.itemStack, protocolVersion);
        });
    }

    public BaseItemStack itemStack() {
        this.lazyBuffer.read();
        return this.itemStack;
    }

    public SetSlot itemStack(BaseItemStack stack) {
        this.lazyBuffer = LazyBuffer.empty();
        this.itemStack = stack;
        return this;
    }

    private void readEntirePacket(ByteBuf buf){
        int index = buf.readerIndex();
        packetData = new byte[buf.readableBytes()];
        buf.readBytes(packetData);
        buf.readerIndex(index);
    }

}
