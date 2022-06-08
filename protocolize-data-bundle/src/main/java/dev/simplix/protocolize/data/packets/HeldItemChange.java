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
public class HeldItemChange extends AbstractPacket {

    public static final List<ProtocolIdMapping> CLIENTBOUND_MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x09),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x37),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x39),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x3A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x3D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x3F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x40),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_4, 0x3F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x48),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_LATEST, 0x47)
    );

    public static final List<ProtocolIdMapping> SERVERBOUND_MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x09),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x17),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12_2, 0x1A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x21),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_15_2, 0x23),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x24),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_LATEST, 0x25)
    );

    private short newSlot;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (packetDirection == PacketDirection.SERVERBOUND) {
            newSlot = buf.readShort();
        } else {
            newSlot = buf.readByte();
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (packetDirection == PacketDirection.SERVERBOUND) {
            buf.writeShort(newSlot);
        } else {
            buf.writeByte(newSlot);
        }
    }

}
