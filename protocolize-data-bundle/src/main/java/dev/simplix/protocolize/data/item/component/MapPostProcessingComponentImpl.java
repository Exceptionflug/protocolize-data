package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MapPostProcessingComponent;
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
public class MapPostProcessingComponentImpl implements MapPostProcessingComponent {

    private PostProcessingType postProcessingType;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        postProcessingType = PostProcessingType.values()[ProtocolUtil.readVarInt(byteBuf)];
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, postProcessingType.ordinal());
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<MapPostProcessingComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 28)
        );

        @Override
        public MapPostProcessingComponent create(PostProcessingType postProcessingType) {
            return new MapPostProcessingComponentImpl(postProcessingType);
        }


        @Override
        public String getName() {
            return "minecraft:map_post_processing";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public MapPostProcessingComponent createEmpty() {
            return new MapPostProcessingComponentImpl(PostProcessingType.LOCK);
        }

    }

}
