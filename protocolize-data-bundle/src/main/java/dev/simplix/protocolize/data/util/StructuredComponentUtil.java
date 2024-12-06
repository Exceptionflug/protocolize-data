package dev.simplix.protocolize.data.util;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BlockPredicate;
import dev.simplix.protocolize.api.item.ConsumeEffect;
import dev.simplix.protocolize.api.item.Firework;
import dev.simplix.protocolize.api.item.MobEffectInstance;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolStringMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Block;
import dev.simplix.protocolize.data.ConsumeEffectType;
import dev.simplix.protocolize.data.EntityType;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.MobEffect;
import dev.simplix.protocolize.data.Sound;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "Protocolize")
public class StructuredComponentUtil {

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

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
            if (!(mapping instanceof ProtocolStringMapping)) {
                logMappingWarning(soundEvent.getSound().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 1);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolStringMapping) mapping).protocolId() + 1);
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
            blockPredicate.setBlockSet(StructuredComponentUtil.readHolderSet(byteBuf, Block.class, protocolVersion));
        }
        if(byteBuf.readBoolean()){
            int propertyCount = ProtocolUtil.readVarInt(byteBuf);
            List<BlockPredicate.Property> properties = new ArrayList<>(propertyCount);
            for(int j = 0; j < propertyCount; j++){
                properties.add(StructuredComponentUtil.readProperty(byteBuf, protocolVersion));
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
            StructuredComponentUtil.writeHolderSet(byteBuf, blockPredicate.getBlockSet(), Block.class, protocolVersion);
        }
        boolean hasProperties = blockPredicate.getProperties() != null && !blockPredicate.getProperties().isEmpty();
        byteBuf.writeBoolean(hasProperties);
        if(hasProperties){
            for(BlockPredicate.Property property : blockPredicate.getProperties()){
                StructuredComponentUtil.writeProperty(byteBuf, property, protocolVersion);
            }
        }
        boolean hasNbtData = blockPredicate.getNbtData() != null;
        byteBuf.writeBoolean(hasNbtData);
        if(hasNbtData){
            NamedBinaryTagUtil.writeTag(byteBuf, blockPredicate.getNbtData(), protocolVersion);
        }
    }

    public static MobEffectInstance readMobEffectInstance(ByteBuf byteBuf, int protocolVersion) {
        MobEffect mobEffect = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, MobEffect.class);
        MobEffectInstance.Details details = StructuredComponentUtil.readMobEffectDetails(byteBuf, protocolVersion);
        return new MobEffectInstance(mobEffect, details);
    }

    public static void writeMobEffectInstance(ByteBuf byteBuf, MobEffectInstance mobEffectInstance, int protocolVersion) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(mobEffectInstance.getMobEffect(), protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            logMappingWarning(mobEffectInstance.getMobEffect().name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
            StructuredComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, new MobEffectInstance.Details(0, 0, false, false, false, null));
            return;
        }
        ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
        StructuredComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, mobEffectInstance.getDetails());
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

    public static boolean writeItemMapping(ByteBuf byteBuf, ItemType itemType, int protocolVersion) {
        return writeItemMapping(byteBuf, itemType, protocolVersion, 0);
    }

    public static boolean writeItemMapping(ByteBuf byteBuf, ItemType itemType, int protocolVersion, int offset) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(itemType, protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            logMappingWarning(itemType.name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
            return false;
        }
        ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id() + offset);
        return true;
    }

    public static ConsumeEffect.ConsumeEffectInstance readConsumeEffect(ByteBuf byteBuf, int protocolVersion) {
        ConsumeEffectType type = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, ConsumeEffectType.class);

        switch (type.name()) {
            case "APPLY_EFFECTS": {
                int count = ProtocolUtil.readVarInt(byteBuf);
                List<MobEffectInstance> effects = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    effects.add(readMobEffectInstance(byteBuf, protocolVersion));
                }
                float probability = byteBuf.readFloat();
                return new ConsumeEffect.ApplyStatusEffects(effects, probability);
            }
            case "REMOVE_EFFECTS": {
                return new ConsumeEffect.RemoveStatusEffects(readHolderSet(byteBuf, MobEffect.class, protocolVersion));
            }
            case "CLEAR_ALL_EFFECTS":
                return new ConsumeEffect.ClearAllStatusEffects();
            case "TELEPORT_RANDOMLY":
                float diameter = byteBuf.readFloat();
                return new ConsumeEffect.TeleportRandomly(diameter);
            case "PLAY_SOUND":
                SoundEvent sound = readSoundEvent(byteBuf, protocolVersion);
                return new ConsumeEffect.PlaySound(sound);
            default:
                throw new IllegalArgumentException("Invalid ConsumeEffectType " + type.name());
        }
    }

    public static void writeConsumeEffect(ByteBuf byteBuf, ConsumeEffect.ConsumeEffectInstance effectInstance, int protocolVersion){
        ProtocolMapping consumeEffectTypeMapping = MAPPING_PROVIDER.mapping(effectInstance.getType(), protocolVersion);
        if (!(consumeEffectTypeMapping instanceof ProtocolIdMapping)) {
            logMappingWarning(effectInstance.getType().name(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, 0);
            return;
        }
        ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) consumeEffectTypeMapping).id());
        if(effectInstance instanceof ConsumeEffect.ApplyStatusEffects){
            ConsumeEffect.ApplyStatusEffects applyStatusEffects = (ConsumeEffect.ApplyStatusEffects) effectInstance;
            ProtocolUtil.writeVarInt(byteBuf, applyStatusEffects.getEffects().size());
            for(MobEffectInstance effect : applyStatusEffects.getEffects()){
                writeMobEffectInstance(byteBuf, effect, protocolVersion);
            }
            byteBuf.writeFloat(applyStatusEffects.getProbability());
        } else if (effectInstance instanceof ConsumeEffect.RemoveStatusEffects) {
            ConsumeEffect.RemoveStatusEffects removeStatusEffects = (ConsumeEffect.RemoveStatusEffects) effectInstance;
            writeHolderSet(byteBuf, removeStatusEffects.getEffects(), MobEffect.class, protocolVersion);
        } else if (effectInstance instanceof ConsumeEffect.ClearAllStatusEffects) {
            ConsumeEffect.ClearAllStatusEffects clearAllStatusEffects = (ConsumeEffect.ClearAllStatusEffects) effectInstance;
            // do nothing here
        } else if (effectInstance instanceof ConsumeEffect.TeleportRandomly) {
            ConsumeEffect.TeleportRandomly teleportRandomly = (ConsumeEffect.TeleportRandomly) effectInstance;
            byteBuf.writeFloat(teleportRandomly.getDiameter());
        } else if (effectInstance instanceof ConsumeEffect.PlaySound) {
            ConsumeEffect.PlaySound playSound = (ConsumeEffect.PlaySound) effectInstance;
            writeSoundEvent(byteBuf, playSound.getSound(), protocolVersion);
        }
    }

    public static <T extends Enum<T>> Either<String, List<T>> readHolderSet(ByteBuf byteBuf, Class<T> clazz, int protocolVersion) {
        int count = ProtocolUtil.readVarInt(byteBuf) - 1;
        if(count == -1){
            return Either.left(ProtocolUtil.readString(byteBuf));
        } else {
            List<T> list = new ArrayList<>(count);
            for(int i = 0; i < count; i++){
                list.add(MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, clazz));
            }
            return Either.right(list);
        }
    }

    public static <T extends Enum<T>> void writeHolderSet(ByteBuf byteBuf, Either<String, List<T>> holderSet, Class<T> clazz, int protocolVersion) {
        ProtocolUtil.writeVarInt(byteBuf, holderSet.isRight() ? holderSet.getRight().size() + 1 : 0);
        if(holderSet.isRight()){
            for(T item : holderSet.getRight()){
                ProtocolMapping mapping = MAPPING_PROVIDER.mapping(item, protocolVersion);
                if (!(mapping instanceof ProtocolIdMapping)) {
                    logMappingWarning(item.name(), protocolVersion);
                    ProtocolUtil.writeVarInt(byteBuf, 0);
                    return;
                }
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
        } else {
            ProtocolUtil.writeString(byteBuf, holderSet.getLeft());
        }
    }

    public static void logMappingWarning(String name, int protocolVersion){
        log.warn("{} cannot be used on protocol version {}", name, protocolVersion);
    }
}
