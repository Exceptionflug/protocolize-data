package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.MobEffectInstance;
import dev.simplix.protocolize.api.item.component.PotionContentsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.MobEffect;
import dev.simplix.protocolize.data.Potion;
import dev.simplix.protocolize.data.util.StructureComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class PotionContentsComponentImpl implements PotionContentsComponent {

    private Potion potion;
    private Integer customColor;
    private List<MobEffectInstance> customEffects;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(byteBuf.readBoolean()) {
            potion = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, Potion.class);
        }
        if (byteBuf.readBoolean()) {
            customColor = byteBuf.readInt();
        }
        int count = ProtocolUtil.readVarInt(byteBuf);
        for (int i = 0; i < count; i++) {
            MobEffect mobEffect = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, MobEffect.class);
            MobEffectInstance.Details details = StructureComponentUtil.readMobEffectDetails(byteBuf, protocolVersion);
            customEffects.add(new MobEffectInstance(mobEffect, details));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeBoolean(potion != null);
        if(potion != null) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(potion, protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", potion.name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
        }
        byteBuf.writeBoolean(customColor != null);
        if(customColor != null) {
            byteBuf.writeInt(customColor);
        }
        ProtocolUtil.writeVarInt(byteBuf, customEffects.size());
        for (MobEffectInstance effect : customEffects) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(effect.getMobEffect(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", effect.getMobEffect().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
                StructureComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, new MobEffectInstance.Details(0, 0, false, false, false, null));
                return;
            }
            ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            StructureComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, effect.getDetails());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addCustomEffect(MobEffectInstance potionEffect) {
        customEffects.add(potionEffect);
    }

    @Override
    public void removeCustomEffect(MobEffectInstance potionEffect) {
        customEffects.remove(potionEffect);
    }

    @Override
    public void removeAllCustomEffects() {
        customEffects.clear();
    }

    public static class Type implements StructuredComponentType<PotionContentsComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 31)
        );

        @Override
        public PotionContentsComponent create(Potion potion) {
            return new PotionContentsComponentImpl(potion, null, new ArrayList<>());
        }

        @Override
        public PotionContentsComponent create(Potion potion, int customColor) {
            return new PotionContentsComponentImpl(potion, customColor, new ArrayList<>());
        }

        @Override
        public PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects) {
            return new PotionContentsComponentImpl(potion, customColor, customEffects);
        }

        @Override
        public PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects) {
            return new PotionContentsComponentImpl(potion, null, customEffects);
        }

        @Override
        public String getName() {
            return "minecraft:potion_contents";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public PotionContentsComponent createEmpty() {
            return new PotionContentsComponentImpl(null, null, new ArrayList<>());
        }

    }

}
