package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.StoredEnchantmentsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Enchantment;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class StoredEnchantmentsComponentImpl implements StoredEnchantmentsComponent {

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
                log.warn("{} cannot be used on protocol version {}", entry.getKey().name(), protocolVersion);
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

    public static class Type implements StructuredComponentType<StoredEnchantmentsComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 23)
        );

        @Override
        public StoredEnchantmentsComponent create(Map<Enchantment, Integer> enchantments) {
            return new StoredEnchantmentsComponentImpl(enchantments, true);
        }

        @Override
        public StoredEnchantmentsComponent create(Map<Enchantment, Integer> enchantments, boolean showInTooltip) {
            return new StoredEnchantmentsComponentImpl(enchantments, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:stored_enchantments";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public StoredEnchantmentsComponent createEmpty() {
            return new StoredEnchantmentsComponentImpl(new HashMap<>(0), true);
        }

    }

}
