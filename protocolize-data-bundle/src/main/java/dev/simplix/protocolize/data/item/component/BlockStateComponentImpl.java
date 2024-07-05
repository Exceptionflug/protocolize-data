package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.BlockStateComponent;
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
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class BlockStateComponentImpl implements BlockStateComponent {

    private Map<String, String> properties;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for (int i = 0; i < count; i++) {
            properties.put(ProtocolUtil.readString(byteBuf), ProtocolUtil.readString(byteBuf));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, properties.size());
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            ProtocolUtil.writeString(byteBuf, entry.getKey());
            ProtocolUtil.writeString(byteBuf, entry.getValue());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public void removeProperty(String key) {
        properties.remove(key);
    }

    @Override
    public void removeAllProperties() {
        properties.clear();
    }

    public static class Type implements StructuredComponentType<BlockStateComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 53),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 52)
        );

        @Override
        public BlockStateComponent create(Map<String, String> properties) {
            return new BlockStateComponentImpl(properties);
        }

        @Override
        public String getName() {
            return "minecraft:block_state";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public BlockStateComponent createEmpty() {
            return new BlockStateComponentImpl(new HashMap<>(0));
        }

    }

}
