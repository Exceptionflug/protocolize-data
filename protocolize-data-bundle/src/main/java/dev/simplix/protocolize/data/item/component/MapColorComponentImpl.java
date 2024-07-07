package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MapColorComponent;
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
public class MapColorComponentImpl implements MapColorComponent {

    private int color;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        color = byteBuf.readInt();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeInt(color);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<MapColorComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 25)
        );

        @Override
        public MapColorComponent create(int id) {
            return new MapColorComponentImpl(id);
        }

        @Override
        public String getName() {
            return "minecraft:map_color";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public MapColorComponent createEmpty() {
            return new MapColorComponentImpl(0);
        }

    }

}
