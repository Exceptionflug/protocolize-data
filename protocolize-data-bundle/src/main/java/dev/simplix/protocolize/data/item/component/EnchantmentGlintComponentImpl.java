package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.EnchantmentGlintComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

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

        @Override
        public EnchantmentGlintComponent create(boolean enabled) {
            return new EnchantmentGlintComponentImpl(enabled);
        }

        @Override
        public String getName() {
            return "minecraft:enchantment_glint_override";
        }

        @Override
        public EnchantmentGlintComponent createEmpty() {
            return new EnchantmentGlintComponentImpl(false);
        }

    }

}
