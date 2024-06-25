package dev.simplix.protocolize.data.packets;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.inventory.InventoryType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
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

    private static final List<String> unknownLegacyTypes = new ArrayList<>();
    private static final List<Integer> unknownTypes = new ArrayList<>();

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x2D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x13),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x14),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x2E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x2F),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x2E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x2D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x2E),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_1_19, 0x2B),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_19_2, 0x2D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_1_19_3, 0x2C),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_4, MINECRAFT_1_20_1, 0x30),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_2, MINECRAFT_1_20_4, 0x31),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 0x33)
    );

    public OpenWindow(int windowId, InventoryType inventoryType, ChatElement<?> title) {
        this.windowId = windowId;
        this.inventoryType = inventoryType;
        this.title = title;
    }

    private int windowId;
    private InventoryType inventoryType;
    private ChatElement<?> title;
    protected int typeFallback;
    protected String legacyTypeFallback;
    protected int legacySizeFallback;

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_14) {
            this.windowId = buf.readUnsignedByte();
            String legacyId = ProtocolUtil.readString(buf);
            this.title = ChatElement.ofJson(ProtocolUtil.readString(buf));
            int size = buf.readUnsignedByte();
            this.inventoryType = InventoryType.type(legacyId, size, protocolVersion);
            if (this.inventoryType == null) {
                legacyTypeFallback = legacyId;
                legacySizeFallback = size;
                if(!unknownLegacyTypes.contains(legacyId)) {
                    unknownLegacyTypes.add(legacyId);
                    log.warn("Unknown inventory type " + legacyId + " in protocol version " + protocolVersion + " for requested size " + size);
                }
            }
            buf.readBytes(buf.readableBytes()); // Skip optional entity id
        } else {
            this.windowId = ProtocolUtil.readVarInt(buf);
            int id = ProtocolUtil.readVarInt(buf);
            this.inventoryType = InventoryType.type(id, protocolVersion);
            if (this.inventoryType == null) {
                typeFallback = id;
                if(!unknownTypes.contains(id)) {
                    unknownTypes.add(id);
                    log.warn("Unknown inventory type " + id + " in protocol version " + protocolVersion);
                }
            }
            if (protocolVersion >= MINECRAFT_1_20_3) {
                try {
                    this.title = ChatElement.ofNbt(NamedBinaryTagUtil.readTag(buf, protocolVersion));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.title = ChatElement.ofJson(ProtocolUtil.readString(buf));
            }
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_14) {
            buf.writeByte(this.windowId & 0xFF);
            ProtocolUtil.writeString(buf, this.inventoryType == null ? legacyTypeFallback : Objects.requireNonNull(this.inventoryType.legacyTypeId(protocolVersion)));
            ProtocolUtil.writeString(buf, this.title.asJson());
            buf.writeByte(this.inventoryType == null ? legacySizeFallback : (this.inventoryType.shouldInventorySizeNotBeZero() ? this.inventoryType.getTypicalSize(protocolVersion) & 0xFF : 0));
        } else {
            ProtocolUtil.writeVarInt(buf, this.windowId);
            ProtocolUtil.writeVarInt(buf, this.inventoryType == null ? typeFallback : this.inventoryType.getTypeId(protocolVersion));
            if (protocolVersion >= MINECRAFT_1_20_3) {
                try {
                    NamedBinaryTagUtil.writeTag(buf, this.title.asNbt(), protocolVersion);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ProtocolUtil.writeString(buf, this.title.asJson());
            }
        }
    }

}
