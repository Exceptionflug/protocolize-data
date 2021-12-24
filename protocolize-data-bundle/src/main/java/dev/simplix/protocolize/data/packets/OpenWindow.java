package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.inventory.InventoryType;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class OpenWindow extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x2D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x2E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x2F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x2E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x2D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_LATEST, 0x2E)
    );

    private int windowId;
    private InventoryType inventoryType;

    /**
     * @since Protocol < 477: legacy text; Protocol >= 477: json component
     */
    private String titleJson;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_14) {
            windowId = buf.readUnsignedByte();
            String legacyId = ProtocolUtil.readString(buf);
            titleJson = ProtocolUtil.readString(buf);
            int size = buf.readUnsignedByte();
            inventoryType = InventoryType.type(legacyId, size, protocolVersion);
            if (inventoryType == null) {
                log.warn("Unknown inventory type " + legacyId + " in protocol version " + protocolVersion + " for requested size " + size);
            }
            buf.readBytes(buf.readableBytes()); // Skip optional entity id
        } else {
            windowId = ProtocolUtil.readVarInt(buf);
            int id = ProtocolUtil.readVarInt(buf);
            inventoryType = InventoryType.type(id, protocolVersion);
            if (inventoryType == null) {
                log.warn("Unknown inventory type " + id + " in protocol version " + protocolVersion);
            }
            titleJson = ProtocolUtil.readString(buf);
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_14) {
            buf.writeByte(windowId & 0xFF);
            ProtocolUtil.writeString(buf, Objects.requireNonNull(inventoryType.legacyTypeId(protocolVersion)));
            ProtocolUtil.writeString(buf, titleJson);
            buf.writeByte(inventoryType.getTypicalSize(protocolVersion) & 0xFF);
        } else {
            ProtocolUtil.writeVarInt(buf, windowId);
            ProtocolUtil.writeVarInt(buf, inventoryType.getTypeId(protocolVersion));
            ProtocolUtil.writeString(buf, titleJson);
        }
    }

}
