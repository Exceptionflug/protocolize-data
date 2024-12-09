package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MapColorComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

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

        @Override
        public MapColorComponent create(int id) {
            return new MapColorComponentImpl(id);
        }

        @Override
        public String getName() {
            return "minecraft:map_color";
        }

        @Override
        public MapColorComponent createEmpty() {
            return new MapColorComponentImpl(0);
        }

    }

}
