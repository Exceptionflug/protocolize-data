package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 27.08.2021
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
public class ConfirmTransaction extends AbstractPacket {

    public static final List<ProtocolIdMapping> CLIENTBOUND_MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x32),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x11),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_14_4, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x11)
    );

    public static final List<ProtocolIdMapping> SERVERBOUND_MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x05),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x06),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x05),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x06),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_16_4, 0x07)
    );

    private int windowId;
    private short actionNumber;
    private boolean accepted;

    @Override
    public void read(ByteBuf buf, PacketDirection direction, int protocolVersion) {
        if (protocolVersion >= MINECRAFT_1_12 && direction == PacketDirection.CLIENTBOUND) {
            windowId = buf.readUnsignedByte();
        } else {
            windowId = buf.readByte();
        }
        actionNumber = buf.readShort();
        accepted = buf.readBoolean();
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int i) {
        buf.writeByte(windowId);
        buf.writeShort(actionNumber);
        buf.writeBoolean(accepted);
    }
}
