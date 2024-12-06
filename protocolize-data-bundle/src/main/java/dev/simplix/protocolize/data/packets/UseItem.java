package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.Hand;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
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
public class UseItem extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_11_1, 0x1D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12_2, 0x20),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x2A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_15_2, 0x2D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x2E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_18_2, 0x2F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19, 0x31),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_20_1, 0x32),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_1_20_2, 0x35),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_3, MINECRAFT_1_20_4, 0x36),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_21_1, 0x39),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_2, MINECRAFT_1_21_3, 0x3B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21_4, MINECRAFT_LATEST, 0x3D)
    );

    private Hand hand;

    /**
     * @since protocol version 759 (1.19)
     */
    private int sequence;

    /**
     * @since protocol version 767 (1.21)
     */
    private float yRot;
    /**
     * @since protocol version 767 (1.21)
     */
    private float xRot;


    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        hand = Hand.handByProtocolId(ProtocolUtil.readVarInt(buf));
        if (protocolVersion >= MINECRAFT_1_19) {
            sequence = ProtocolUtil.readVarInt(buf);
        }
        if (protocolVersion >= MINECRAFT_1_21) {
            yRot = buf.readFloat();
            xRot = buf.readFloat();
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        ProtocolUtil.writeVarInt(buf, hand.protocolId());
        if (protocolVersion >= MINECRAFT_1_19) {
            ProtocolUtil.writeVarInt(buf, sequence);
        }
        if (protocolVersion >= MINECRAFT_1_21) {
            buf.writeFloat(yRot);
            buf.writeFloat(xRot);
        }
    }

}
