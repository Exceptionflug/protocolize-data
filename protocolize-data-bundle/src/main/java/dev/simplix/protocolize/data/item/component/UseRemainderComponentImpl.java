package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.UseRemainderComponent;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UseRemainderComponentImpl implements UseRemainderComponent {

    private BaseItemStack convertInto;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        convertInto = ItemStackSerializer.read(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ItemStackSerializer.write(byteBuf, convertInto, protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<UseRemainderComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public UseRemainderComponent create(BaseItemStack convertInto) {
            return new UseRemainderComponentImpl(convertInto);
        }

        @Override
        public String getName() {
            return "minecraft:use_remainder";
        }

        @Override
        public UseRemainderComponent createEmpty() {
            return new UseRemainderComponentImpl(null);
        }

    }


}
