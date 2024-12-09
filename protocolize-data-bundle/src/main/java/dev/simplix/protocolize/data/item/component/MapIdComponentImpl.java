package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MapIdComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapIdComponentImpl implements MapIdComponent {

    private int id;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        id = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, id);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<MapIdComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public MapIdComponent create(int id) {
            return new MapIdComponentImpl(id);
        }

        @Override
        public String getName() {
            return "minecraft:map_id";
        }

        @Override
        public MapIdComponent createEmpty() {
            return new MapIdComponentImpl(0);
        }

    }

}
