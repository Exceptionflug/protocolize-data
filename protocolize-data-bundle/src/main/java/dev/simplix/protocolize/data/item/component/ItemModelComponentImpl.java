package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.ItemModelComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemModelComponentImpl implements ItemModelComponent {

    private String model;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        model = ProtocolUtil.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeString(byteBuf, model);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<ItemModelComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public ItemModelComponent create(String model) {
            return new ItemModelComponentImpl(model);
        }

        @Override
        public String getName() {
            return "minecraft:item_model";
        }

        @Override
        public ItemModelComponent createEmpty() {
            return new ItemModelComponentImpl("");
        }

    }

}
