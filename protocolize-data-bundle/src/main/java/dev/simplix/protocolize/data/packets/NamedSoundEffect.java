package dev.simplix.protocolize.data.packets;

import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolStringMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Sound;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@ToString
@Getter
@Setter
@Slf4j
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NamedSoundEffect extends AbstractPacket {

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x29),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x19),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x1A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x19),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x1A),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x19),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x18),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_LATEST, 0x19)
    );
    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();
    private Sound sound;
    private SoundCategory category;
    private double x;
    private double y;
    private double z;
    private float volume;
    private float pitch;

    @Override
    public void read(ByteBuf buf, PacketDirection direction, int protocolVersion) {
        String id = ProtocolUtil.readString(buf);
        sound = lookupSound(id, protocolVersion);
        if (sound == null) {
            log.warn("Don't know what sound " + id + " at protocol " + protocolVersion + " should be.");
            return;
        }
        if (protocolVersion > MINECRAFT_1_8)
            category = SoundCategory.values()[ProtocolUtil.readVarInt(buf)];
        x = buf.readInt() / 8D;
        y = buf.readInt() / 8D;
        z = buf.readInt() / 8D;
        volume = buf.readFloat();
        if (protocolVersion < MINECRAFT_1_10) {
            pitch = buf.readUnsignedByte() / 63F;
        } else {
            pitch = buf.readFloat();
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection direction, int protocolVersion) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(sound, protocolVersion);
        if (mapping == null) {
            log.warn("Don't know what sound " + sound.name() + " at protocol " + protocolVersion + " should be.");
            return;
        }
        ProtocolUtil.writeString(buf, ((ProtocolStringMapping) mapping).id());
        if (protocolVersion > MINECRAFT_1_8)
            ProtocolUtil.writeVarInt(buf, category.ordinal());
        buf.writeInt((int) (x * 8));
        buf.writeInt((int) (y * 8));
        buf.writeInt((int) (z * 8));
        buf.writeFloat(volume);
        if (protocolVersion < MINECRAFT_1_10) {
            buf.writeByte((byte) (pitch * 63) & 0xFF);
        } else {
            buf.writeFloat(pitch);
        }
    }

    private Sound lookupSound(String id, int protocolVersion) {
        Multimap<Sound, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(Sound.class, protocolVersion);
        for (Sound sound : mappings.keySet()) {
            for (ProtocolMapping mapping : mappings.get(sound)) {
                if (mapping instanceof ProtocolStringMapping) {
                    if (((ProtocolStringMapping) mapping).id().equals(id)) {
                        return sound;
                    }
                }
            }
        }
        return null;
    }

}
