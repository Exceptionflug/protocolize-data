package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.EnchantmentsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class EnchantmentsComponentImpl implements EnchantmentsComponent {

    private Map<Integer, Integer> enchantments;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        enchantments = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            int id = ProtocolUtil.readVarInt(byteBuf);
            int level = ProtocolUtil.readVarInt(byteBuf);
            enchantments.put(id, level);
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, enchantments.size());
        for (Map.Entry<Integer, Integer> entry : enchantments.entrySet()) {
            ProtocolUtil.writeVarInt(byteBuf, entry.getKey());
            ProtocolUtil.writeVarInt(byteBuf, entry.getValue());
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void removeEnchantment(int id) {
        enchantments.remove(id);
    }

    @Override
    public void addEnchantment(int id, int level) {
        enchantments.put(id, level);
    }

    @Override
    public void removeAllEnchantments() {
        enchantments.clear();
    }

    public static class Type implements StructuredComponentType<EnchantmentsComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 9)
        );

        @Override
        public EnchantmentsComponent create(Map<Integer, Integer> enchantments) {
            return new EnchantmentsComponentImpl(enchantments, true);
        }

        @Override
        public EnchantmentsComponent create(Map<Integer, Integer> enchantments, boolean showInTooltip) {
            return new EnchantmentsComponentImpl(enchantments, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:enchantments";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public EnchantmentsComponent createEmpty() {
            return new EnchantmentsComponentImpl(new HashMap<>(0), true);
        }

    }

}
