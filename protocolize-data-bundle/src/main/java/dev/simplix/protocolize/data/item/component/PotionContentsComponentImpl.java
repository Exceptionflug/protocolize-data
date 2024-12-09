package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.MobEffectInstance;
import dev.simplix.protocolize.api.item.component.PotionContentsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.MobEffect;
import dev.simplix.protocolize.data.Potion;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class PotionContentsComponentImpl implements PotionContentsComponent {

    private Potion potion;
    private Integer customColor;
    private List<MobEffectInstance> customEffects;
    private String customName;

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
            customEffects.add(StructuredComponentUtil.readMobEffectInstance(byteBuf, protocolVersion));
        }
        if(protocolVersion >= MINECRAFT_1_21_2) {
            if (byteBuf.readBoolean()) {
                customName = ProtocolUtil.readString(byteBuf);
            }
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeBoolean(potion != null);
        if(potion != null) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(potion, protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                StructuredComponentUtil.logMappingWarning(potion.name(), protocolVersion);
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
            StructuredComponentUtil.writeMobEffectInstance(byteBuf, effect, protocolVersion);
        }
        if(protocolVersion >= MINECRAFT_1_21_2) {
            byteBuf.writeBoolean(customName != null);
            if (customName != null) {
                ProtocolUtil.writeString(byteBuf, customName);
            }
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

        @Override
        public PotionContentsComponent create(Potion potion) {
            return new PotionContentsComponentImpl(potion, null, new ArrayList<>(), null);
        }

        @Override
        public PotionContentsComponent create(Potion potion, int customColor) {
            return new PotionContentsComponentImpl(potion, customColor, new ArrayList<>(), null);
        }

        @Override
        public PotionContentsComponent create(Potion potion, int customColor, String customName) {
            return new PotionContentsComponentImpl(potion, customColor, new ArrayList<>(), customName);
        }

        @Override
        public PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects) {
            return new PotionContentsComponentImpl(potion, customColor, customEffects, null);
        }

        @Override
        public PotionContentsComponent create(Potion potion, int customColor, List<MobEffectInstance> customEffects, String customName) {
            return new PotionContentsComponentImpl(potion, customColor, customEffects, customName);
        }

        @Override
        public PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects) {
            return new PotionContentsComponentImpl(potion, null, customEffects, null);
        }

        @Override
        public PotionContentsComponent create(Potion potion, List<MobEffectInstance> customEffects, String customName) {
            return new PotionContentsComponentImpl(potion, null, customEffects, customName);
        }

        @Override
        public String getName() {
            return "minecraft:potion_contents";
        }

        @Override
        public PotionContentsComponent createEmpty() {
            return new PotionContentsComponentImpl(null, null, new ArrayList<>(), null);
        }

    }

}
