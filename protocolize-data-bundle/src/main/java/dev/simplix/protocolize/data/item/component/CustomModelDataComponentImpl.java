package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CustomModelDataComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomModelDataComponentImpl implements CustomModelDataComponent {

    private int customModelData;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        customModelData = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, customModelData);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<CustomModelDataComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public CustomModelDataComponent create(int customModelData) {
            return new CustomModelDataComponentImpl(customModelData);
        }

        @Override
        public String getName() {
            return "minecraft:custom_model_data";
        }

        @Override
        public CustomModelDataComponent createEmpty() {
            return new CustomModelDataComponentImpl(0);
        }

    }

}
