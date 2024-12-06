package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.SuspiciousStewEffectsComponent;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.MobEffect;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class SuspiciousStewEffectsComponentImpl implements SuspiciousStewEffectsComponent {

    private Map<MobEffect, Integer> effects;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for (int i = 0; i < count; i++) {
            MobEffect mobEffect = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, MobEffect.class);
            int duration = ProtocolUtil.readVarInt(byteBuf);
            effects.put(mobEffect, duration);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, effects.size());
        for (Map.Entry<MobEffect,Integer> entry : effects.entrySet()) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(entry.getKey(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                StructuredComponentUtil.logMappingWarning(entry.getKey().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
                ProtocolUtil.writeVarInt(byteBuf, 0);
                return;
            }
            ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            ProtocolUtil.writeVarInt(byteBuf, entry.getValue());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addEffect(MobEffect effect, int duration) {
        effects.put(effect, duration);
    }

    @Override
    public void removeEffect(MobEffect effect) {
        effects.remove(effect);
    }

    @Override
    public void removeAllEffects() {
        effects.clear();
    }

    public static class Type implements StructuredComponentType<SuspiciousStewEffectsComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public SuspiciousStewEffectsComponent create(Map<MobEffect, Integer> effects) {
            return new SuspiciousStewEffectsComponentImpl(effects);
        }

        @Override
        public String getName() {
            return "minecraft:suspicious_stew_effects";
        }

        @Override
        public SuspiciousStewEffectsComponent createEmpty() {
            return new SuspiciousStewEffectsComponentImpl(new HashMap<>(0));
        }

    }

}
