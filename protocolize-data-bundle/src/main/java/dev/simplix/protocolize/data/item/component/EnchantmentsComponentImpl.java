package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.EnchantmentsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Enchantment;
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
public class EnchantmentsComponentImpl implements EnchantmentsComponent {

    private Map<Enchantment, Integer> enchantments;
    private boolean showInTooltip;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        enchantments = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            Enchantment enchantment = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, Enchantment.class);
            int level = ProtocolUtil.readVarInt(byteBuf);
            enchantments.put(enchantment, level);
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, enchantments.size());
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(entry.getKey(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                StructuredComponentUtil.logMappingWarning(entry.getKey().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
            ProtocolUtil.writeVarInt(byteBuf, entry.getValue());
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void removeEnchantment(Enchantment enchantment) {
        enchantments.remove(enchantment);
    }

    @Override
    public void addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    @Override
    public void removeAllEnchantments() {
        enchantments.clear();
    }

    public static class Type implements StructuredComponentType<EnchantmentsComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public EnchantmentsComponent create(Map<Enchantment, Integer> enchantments) {
            return new EnchantmentsComponentImpl(enchantments, true);
        }

        @Override
        public EnchantmentsComponent create(Map<Enchantment, Integer> enchantments, boolean showInTooltip) {
            return new EnchantmentsComponentImpl(enchantments, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:enchantments";
        }

        @Override
        public EnchantmentsComponent createEmpty() {
            return new EnchantmentsComponentImpl(new HashMap<>(0), true);
        }

    }

}
