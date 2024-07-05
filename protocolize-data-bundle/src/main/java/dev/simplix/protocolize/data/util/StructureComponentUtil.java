package dev.simplix.protocolize.data.util;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BlockPredicate;
import dev.simplix.protocolize.api.item.BlockSet;
import dev.simplix.protocolize.api.item.Firework;
import dev.simplix.protocolize.api.item.MobEffectInstance;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Sound;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "Protocolize")
public class StructureComponentUtil {

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    public static BlockSet readBlockSet(ByteBuf byteBuf, int protocolVersion){
        BlockSet blockSet = new BlockSet();
        int type = ProtocolUtil.readVarInt(byteBuf);
        if(type == 0){
            blockSet.setIdentifier(ProtocolUtil.readString(byteBuf));
        } else {
            List<Integer> blockIds = new ArrayList<>(type - 1);
            for(int j = 0; j < type - 1; j++){
                blockIds.add(ProtocolUtil.readVarInt(byteBuf));
            }
            blockSet.setBlockIds(blockIds);
        }
        return blockSet;
    }

    public static void writeBlockSet(ByteBuf byteBuf, BlockSet blockSet, int protocolVersion){
        ProtocolUtil.writeVarInt(byteBuf, blockSet.getIdentifier() != null ? 0 : blockSet.getBlockIds().size() + 1);
        if(blockSet.getIdentifier() != null){
            ProtocolUtil.writeString(byteBuf, blockSet.getIdentifier());
        } else {
            for(int blockId : blockSet.getBlockIds()){
                ProtocolUtil.writeVarInt(byteBuf, blockId);
            }
        }
    }

    public static BlockPredicate.Property readProperty(ByteBuf byteBuf, int protocolVersion){
        BlockPredicate.Property property = new BlockPredicate.Property();
        property.setName(ProtocolUtil.readString(byteBuf));
        if(byteBuf.readBoolean()){
            property.setExactValue(ProtocolUtil.readString(byteBuf));
        } else {
            property.setMinValue(ProtocolUtil.readString(byteBuf));
            property.setMaxValue(ProtocolUtil.readString(byteBuf));
        }
        return property;
    }

    public static void writeProperty(ByteBuf byteBuf, BlockPredicate.Property property, int protocolVersion){
        ProtocolUtil.writeString(byteBuf, property.getName());
        byteBuf.writeBoolean(property.getExactValue() != null);
        if(property.getExactValue() != null){
            ProtocolUtil.writeString(byteBuf, property.getExactValue());
        } else {
            ProtocolUtil.writeString(byteBuf, property.getMinValue());
            ProtocolUtil.writeString(byteBuf, property.getMaxValue());
        }
    }

    public static Firework.Meta readFireworkMeta(ByteBuf byteBuf, int protocolVersion){
        Firework.Meta meta = new Firework.Meta();
        meta.setShape(Firework.Meta.Shape.values()[ProtocolUtil.readVarInt(byteBuf)]);
        int count = ProtocolUtil.readVarInt(byteBuf);
        List<Integer> colors = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            colors.add(byteBuf.readInt());
        }
        meta.setColors(colors);
        int fadeCount = ProtocolUtil.readVarInt(byteBuf);
        List<Integer> fadeColors = new ArrayList<>(fadeCount);
        for(int i = 0; i < fadeCount; i++){
            fadeColors.add(byteBuf.readInt());
        }
        meta.setFadeColors(fadeColors);
        meta.setTrail(byteBuf.readBoolean());
        meta.setTwinkle(byteBuf.readBoolean());
        return meta;
    }

    public static void writeFireworkMeta(ByteBuf byteBuf, Firework.Meta meta, int protocolVersion) {
        ProtocolUtil.writeVarInt(byteBuf, meta.getShape().ordinal());
        ProtocolUtil.writeVarInt(byteBuf, meta.getColors().size());
        for(int color : meta.getColors()){
            byteBuf.writeInt(color);
        }
        ProtocolUtil.writeVarInt(byteBuf, meta.getFadeColors().size());
        for(int color : meta.getFadeColors()){
            byteBuf.writeInt(color);
        }
        byteBuf.writeBoolean(meta.isTrail());
        byteBuf.writeBoolean(meta.isTwinkle());
    }

    public static SoundEvent readSoundEvent(ByteBuf byteBuf, int protocolVersion) {
        SoundEvent soundEvent = new SoundEvent();
        int type = ProtocolUtil.readVarInt(byteBuf);
        if(type != 0) {
            soundEvent.setSound(MAPPING_PROVIDER.mapIdToEnum(type - 1, protocolVersion, Sound.class));
        } else {
            soundEvent.setIdentifier(ProtocolUtil.readString(byteBuf));
            if(byteBuf.readBoolean()){
                soundEvent.setFixedRange(byteBuf.readFloat());
            }
        }
        return soundEvent;
    }

    public static void writeSoundEvent(ByteBuf byteBuf, SoundEvent soundEvent, int protocolVersion) {
        if(soundEvent.getSound() != null){
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(soundEvent.getSound(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", soundEvent.getSound().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 1);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id() + 1);
            }
        } else {
            ProtocolUtil.writeString(byteBuf, soundEvent.getIdentifier());
            byteBuf.writeBoolean(soundEvent.getFixedRange() != null);
            if(soundEvent.getFixedRange() != null){
                byteBuf.writeFloat(soundEvent.getFixedRange());
            }
        }
    }

    public static BlockPredicate readBlockPredicate(ByteBuf byteBuf, int protocolVersion) throws IOException {
        BlockPredicate blockPredicate = new BlockPredicate();
        if(byteBuf.readBoolean()){
            blockPredicate.setBlockSet(StructureComponentUtil.readBlockSet(byteBuf, protocolVersion));
        }
        if(byteBuf.readBoolean()){
            int propertyCount = ProtocolUtil.readVarInt(byteBuf);
            List<BlockPredicate.Property> properties = new ArrayList<>(propertyCount);
            for(int j = 0; j < propertyCount; j++){
                properties.add(StructureComponentUtil.readProperty(byteBuf, protocolVersion));
            }
            blockPredicate.setProperties(properties);
        }
        if(byteBuf.readBoolean()){
            blockPredicate.setNbtData((CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
        }
        return blockPredicate;
    }

    public static void writeBlockPredicate(ByteBuf byteBuf, BlockPredicate blockPredicate, int protocolVersion) throws IOException {
        boolean hasBlockSet = blockPredicate.getBlockSet() != null;
        byteBuf.writeBoolean(hasBlockSet);
        if(hasBlockSet){
            StructureComponentUtil.writeBlockSet(byteBuf, blockPredicate.getBlockSet(), protocolVersion);
        }
        boolean hasProperties = blockPredicate.getProperties() != null && !blockPredicate.getProperties().isEmpty();
        byteBuf.writeBoolean(hasProperties);
        if(hasProperties){
            for(BlockPredicate.Property property : blockPredicate.getProperties()){
                StructureComponentUtil.writeProperty(byteBuf, property, protocolVersion);
            }
        }
        boolean hasNbtData = blockPredicate.getNbtData() != null;
        byteBuf.writeBoolean(hasNbtData);
        if(hasNbtData){
            NamedBinaryTagUtil.writeTag(byteBuf, blockPredicate.getNbtData(), protocolVersion);
        }
    }



    public static MobEffectInstance.Details readMobEffectDetails(ByteBuf byteBuf, int protocolVersion) {
        int amplifier = ProtocolUtil.readVarInt(byteBuf);
        int duration = ProtocolUtil.readVarInt(byteBuf);
        boolean ambient = byteBuf.readBoolean();
        boolean showParticles = byteBuf.readBoolean();
        boolean showIcon = byteBuf.readBoolean();
        MobEffectInstance.Details hiddenEffect = byteBuf.readBoolean() ? readMobEffectDetails(byteBuf, protocolVersion) : null;
        return new MobEffectInstance.Details(amplifier, duration, ambient, showParticles, showIcon, hiddenEffect);
    }

    public static void writeMobEffectDetails(ByteBuf byteBuf, int protocolVersion, MobEffectInstance.Details effect) {
        ProtocolUtil.writeVarInt(byteBuf, effect.getAmplifier());
        ProtocolUtil.writeVarInt(byteBuf, effect.getDuration());
        byteBuf.writeBoolean(effect.isAmbient());
        byteBuf.writeBoolean(effect.isShowParticles());
        byteBuf.writeBoolean(effect.isShowIcon());
        byteBuf.writeBoolean(effect.getHiddenEffect() != null);
        if(effect.getHiddenEffect() != null){
            writeMobEffectDetails(byteBuf, protocolVersion, effect.getHiddenEffect());
        }
    }
}
