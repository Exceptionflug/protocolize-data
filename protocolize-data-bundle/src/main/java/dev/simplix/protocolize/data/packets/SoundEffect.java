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
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

/**
 * Date: 20.12.2022
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
public class SoundEffect extends AbstractPacket {

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_9_2, 0x47),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9_3, MINECRAFT_1_11_2, 0x46),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12, MINECRAFT_1_12, 0x48),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_12_1, MINECRAFT_1_12_2, 0x49),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x4D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x51),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x52),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_5, 0x51),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_17_1, 0x5C),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_18, MINECRAFT_1_19, 0x5D),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_1, MINECRAFT_1_19_2, 0x60),
        AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19_3, MINECRAFT_LATEST, 0x5E)
    );

    @Setter(AccessLevel.NONE)
    private Sound supportedSound;
    private SoundCategory category;
    private int soundId = -1;
    private String resourceLocation;
    private double x;
    private double y;
    private double z;
    private float volume;
    private float pitch;
    private Float fixedRange;

    /**
     * @since protocol version 759 (1.19)
     */
    private long seed;

    public SoundEffect(Sound supportedSound, SoundCategory category, double x, double y, double z, float volume, float pitch) {
        this.supportedSound = supportedSound;
        this.category = category;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SoundEffect(int soundId, SoundCategory category, double x, double y, double z, float volume, float pitch) {
        this.category = category;
        this.soundId = soundId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        this.soundId = ProtocolUtil.readVarInt(buf);
        if (this.soundId == 0) {
            // Read resource location
            this.resourceLocation = ProtocolUtil.readString(buf);
            if (buf.readBoolean()) {
                this.fixedRange = buf.readFloat();
            }
        }
        if (protocolVersion > MINECRAFT_1_8) {
            this.category = SoundCategory.values()[ProtocolUtil.readVarInt(buf)];
        }
        this.x = buf.readInt() / 8D;
        this.y = buf.readInt() / 8D;
        this.z = buf.readInt() / 8D;
        this.volume = buf.readFloat();
        if (protocolVersion < MINECRAFT_1_10) {
            this.pitch = buf.readUnsignedByte() / 63F;
        } else {
            this.pitch = buf.readFloat();
        }
        if (protocolVersion >= MINECRAFT_1_19) {
            this.seed = buf.readLong();
        }

        lookUpSound(protocolVersion);
    }

    private void lookUpSound(int protocolVersion) {
        Multimap<Sound, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(Sound.class, protocolVersion);
        for (Sound sound : mappings.keySet()) {
            for (ProtocolMapping mapping : mappings.get(sound)) {
                if (mapping instanceof ProtocolStringMapping) {
                    String id = "minecraft:" + ((ProtocolStringMapping) mapping).id();
                    if (id.equals(this.resourceLocation)) {
                        this.supportedSound = sound;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
        if (this.soundId == -1) {
            lookUpSoundId(protocolVersion);
        }
        ProtocolUtil.writeVarInt(buf, this.soundId);
        if (this.soundId == 0) {
            ProtocolUtil.writeString(buf, this.resourceLocation);
            buf.writeBoolean(this.fixedRange != null);
            if (this.fixedRange != null) {
                buf.writeFloat(this.fixedRange);
            }
        }
        if (protocolVersion > MINECRAFT_1_8) {
            ProtocolUtil.writeVarInt(buf, this.category.ordinal());
        }
        buf.writeInt((int) (this.x * 8));
        buf.writeInt((int) (this.y * 8));
        buf.writeInt((int) (this.z * 8));
        buf.writeFloat(this.volume);
        if (protocolVersion < MINECRAFT_1_10) {
            buf.writeByte((byte) (this.pitch * 63) & 0xFF);
        } else {
            buf.writeFloat(this.pitch);
        }
        if (protocolVersion >= MINECRAFT_1_19) {
            buf.writeLong(this.seed);
        }
    }

    private void lookUpSoundId(int protocolVersion) {
        if (this.supportedSound == null) {
            throw new IllegalStateException("Packet holds no sound information");
        }
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(supportedSound, protocolVersion);
        if (!(mapping instanceof ProtocolStringMapping)) {
            throw new IllegalArgumentException("Unable to play sound effect " + supportedSound.name() +
                " at protocol version " + protocolVersion);
        }
        this.soundId = 0;
        this.resourceLocation = "minecraft:" + ((ProtocolStringMapping) mapping).id();
    }

    public void supportedSound(Sound sound) {
        this.supportedSound = sound;
        this.soundId = -1;
    }

}
