package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.EntityDataComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

@Data
@AllArgsConstructor
public class EntityDataComponentImpl implements EntityDataComponent {

    private CompoundTag data;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException{
        data = (CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        NamedBinaryTagUtil.writeTag(byteBuf, data, protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<EntityDataComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:entity_data";
        }

        @Override
        public EntityDataComponent createEmpty() {
            return new EntityDataComponentImpl(null);
        }

        @Override
        public EntityDataComponent create(CompoundTag compoundTag) {
            return new EntityDataComponentImpl(compoundTag);
        }
    }

}
