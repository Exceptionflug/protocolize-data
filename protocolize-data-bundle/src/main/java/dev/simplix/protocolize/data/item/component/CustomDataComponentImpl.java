package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CustomDataComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

@Data
@AllArgsConstructor
public class CustomDataComponentImpl implements CustomDataComponent {

    private CompoundTag customData;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException{
        customData = (CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        NamedBinaryTagUtil.writeTag(byteBuf, customData, protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<CustomDataComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public String getName() {
            return "minecraft:custom_data";
        }

        @Override
        public CustomDataComponent createEmpty() {
            return new CustomDataComponentImpl(null);
        }

        @Override
        public CustomDataComponent create(CompoundTag compoundTag) {
            return new CustomDataComponentImpl(compoundTag);
        }
    }

}
