package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.EnchantmentGlintComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class EnchantmentGlintComponentImpl implements EnchantmentGlintComponent {

    private boolean showGlint;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        showGlint = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        byteBuf.writeBoolean(showGlint);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<EnchantmentGlintComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 18)
        );

        @Override
        public EnchantmentGlintComponent create(boolean enabled) {
            return new EnchantmentGlintComponentImpl(enabled);
        }

        @Override
        public String getName() {
            return "minecraft:enchantment_glint_override";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public EnchantmentGlintComponent createEmpty() {
            return new EnchantmentGlintComponentImpl(false);
        }

    }

}
