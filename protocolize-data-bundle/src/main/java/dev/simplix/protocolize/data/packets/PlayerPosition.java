package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.Location;
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
 * Date: 29.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class PlayerPosition extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x04),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x0C),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x0D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x10),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_15_2, 0x11),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_4, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x11),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_LATEST, 0x13)
    );

    private Location location;
    private boolean onGround;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int i) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        onGround = buf.readBoolean();

        location = new Location(x, y, z, 0, 0);
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int i) {
        buf.writeDouble(location.x());
        buf.writeDouble(location.y());
        buf.writeDouble(location.z());
        buf.writeBoolean(onGround);
    }

}
