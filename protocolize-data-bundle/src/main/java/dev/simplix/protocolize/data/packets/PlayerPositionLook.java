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
public class PlayerPositionLook extends AbstractPacket {

    /* ServerboundMovePlayerPacket.PosRot */

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x06),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_2, 0x0D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x0F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x0E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x11),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_15_2, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_5, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x12),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_19_2, 0x15),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_1_19_3, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_4, MINECRAFT_1_20_1, 0x15),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_1_20_2, 0x17),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_3, MINECRAFT_1_20_4, 0x18),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_21, 0x1B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_2, MINECRAFT_LATEST, 0x1D)
    );

    private Location location;
    private boolean onGround;
    private boolean horizontalCollision;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        this.location = new Location(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat());
        if(protocolVersion >= MINECRAFT_1_21_2){
            short flags = buf.readUnsignedByte();
            this.onGround = unpackOnGround(flags);
            this.horizontalCollision = unpackHorizontalCollision(flags);
        } else {
            this.onGround = buf.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        buf.writeDouble(this.location.x());
        buf.writeDouble(this.location.y());
        buf.writeDouble(this.location.z());
        buf.writeFloat(this.location.yaw());
        buf.writeFloat(this.location.pitch());
        if(protocolVersion >= MINECRAFT_1_21_2){
            buf.writeByte(packFlags(this.onGround, this.horizontalCollision));
        } else {
            buf.writeBoolean(this.onGround);
        }
    }

    public static int packFlags(boolean onGround, boolean horizontalCollision){
        int i = 0;

        if (onGround) {
            i |= 1;
        }

        if (horizontalCollision) {
            i |= 2;
        }

        return i;
    }

    public static boolean unpackOnGround(short flags){
        return (flags & 1) != 0;
    }

    public static boolean unpackHorizontalCollision(short flags){
        return (flags & 2) != 0;
    }

}
