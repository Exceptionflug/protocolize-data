package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.ConsumeEffect;
import dev.simplix.protocolize.api.item.ItemUseAnimation;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.item.component.ConsumableComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ConsumableComponentImpl implements ConsumableComponent {

    private float consumeSeconds;
    private ItemUseAnimation animation;
    private SoundEvent sound;
    private boolean hasParticles;
    private List<ConsumeEffect.ConsumeEffectInstance> consumeEffects;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        consumeSeconds = byteBuf.readFloat();
        animation = ItemUseAnimation.values()[ProtocolUtil.readVarInt(byteBuf)];
        sound = StructuredComponentUtil.readSoundEvent(byteBuf, protocolVersion);
        hasParticles = byteBuf.readBoolean();
        int effectCount = ProtocolUtil.readVarInt(byteBuf);
        consumeEffects = new ArrayList<>();
        for(int i = 0; i < effectCount; i++) {
            consumeEffects.add(StructuredComponentUtil.readConsumeEffect(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeFloat(consumeSeconds);
        ProtocolUtil.writeVarInt(byteBuf, animation.ordinal());
        StructuredComponentUtil.writeSoundEvent(byteBuf, sound, protocolVersion);
        byteBuf.writeBoolean(hasParticles);
        ProtocolUtil.writeVarInt(byteBuf, consumeEffects.size());
        for(ConsumeEffect.ConsumeEffectInstance effect : consumeEffects) {
            StructuredComponentUtil.writeConsumeEffect(byteBuf, effect, protocolVersion);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<ConsumableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public ConsumableComponent create(float consumeSeconds, ItemUseAnimation animation, SoundEvent sound, boolean hasParticles, List<ConsumeEffect.ConsumeEffectInstance> consumeEffects) {
            return new ConsumableComponentImpl(consumeSeconds, animation, sound, hasParticles, consumeEffects);
        }

        @Override
        public String getName() {
            return "minecraft:consumable";
        }

        @Override
        public ConsumableComponent createEmpty() {
            return new ConsumableComponentImpl(0, ItemUseAnimation.NONE, null, false, new ArrayList<>());
        }
    }
}
