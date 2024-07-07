package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.item.MobEffectInstance;
import dev.simplix.protocolize.api.item.component.FoodComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.MobEffect;
import dev.simplix.protocolize.data.util.StructureComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class FoodComponentImpl implements FoodComponent {

    private int nutrition;
    private float saturation;
    private boolean canAlwaysEat;
    private float secondsToEat;
    private BaseItemStack usingConvertsTo;
    private Map<MobEffectInstance, Float> effects;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        nutrition = ProtocolUtil.readVarInt(byteBuf);
        saturation = byteBuf.readFloat();
        canAlwaysEat = byteBuf.readBoolean();
        secondsToEat = byteBuf.readFloat();
        if(protocolVersion >= MINECRAFT_1_21) {
            if(byteBuf.readBoolean()) {
                usingConvertsTo = ItemStackSerializer.read(byteBuf, protocolVersion);
            }
        }
        int count = ProtocolUtil.readVarInt(byteBuf);
        for (int i = 0; i < count; i++) {
            MobEffect mobEffect = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, MobEffect.class);
            MobEffectInstance.Details details = StructureComponentUtil.readMobEffectDetails(byteBuf, protocolVersion);
            float probability = byteBuf.readFloat();
            effects.put(new MobEffectInstance(mobEffect, details), probability);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, nutrition);
        byteBuf.writeFloat(saturation);
        byteBuf.writeBoolean(canAlwaysEat);
        byteBuf.writeFloat(secondsToEat);
        if(protocolVersion >= MINECRAFT_1_21) {
            boolean hasUsingConvertsTo = usingConvertsTo != null;
            byteBuf.writeBoolean(hasUsingConvertsTo);
            if (hasUsingConvertsTo){
                ItemStackSerializer.write(byteBuf, usingConvertsTo, protocolVersion);
            }
        }
        ProtocolUtil.writeVarInt(byteBuf, effects.size());
        for (Map.Entry<MobEffectInstance, Float> entry : effects.entrySet()) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(entry.getKey().getMobEffect(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", entry.getKey().getMobEffect().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
                StructureComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, new MobEffectInstance.Details(0, 0, false, false, false, null));
                return;
            }
            ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            StructureComponentUtil.writeMobEffectDetails(byteBuf, protocolVersion, entry.getKey().getDetails());
            byteBuf.writeFloat(entry.getValue());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addEffect(MobEffectInstance effect, float probability) {
        effects.put(effect, probability);
    }

    @Override
    public void removeEffect(MobEffectInstance effect) {
        effects.remove(effect);
    }

    @Override
    public void removeAllEffects() {
        effects.clear();
    }

    public static class Type implements StructuredComponentType<FoodComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 20)
        );

        @Override
        public FoodComponent create(int nutrition, float saturation, boolean canAlwaysEat, float secondsToEat, BaseItemStack usingConvertsTo, Map<MobEffectInstance, Float> effects) {
            return new FoodComponentImpl(nutrition, saturation, canAlwaysEat, secondsToEat, usingConvertsTo, effects);
        }

        @Override
        public String getName() {
            return "minecraft:food";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public FoodComponent createEmpty() {
            return new FoodComponentImpl(0, 0, false, 1, ItemStack.NO_DATA, new HashMap<>(0));
        }

    }

}
