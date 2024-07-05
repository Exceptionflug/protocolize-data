package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MaxDamageComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class MaxDamageComponentImpl implements MaxDamageComponent {

    private int maxDamage;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        maxDamage = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, maxDamage);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<MaxDamageComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 2)
        );

        @Override
        public MaxDamageComponent create(int value) {
            return new MaxDamageComponentImpl(value);
        }

        @Override
        public String getName() {
            return "minecraft:max_damage";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public MaxDamageComponent createEmpty() {
            return new MaxDamageComponentImpl(0);
        }

    }

}
