package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.CustomModelDataComponent;
import dev.simplix.protocolize.api.item.component.ItemNameComponent;
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

    public static class Type implements StructuredComponentType<CustomModelDataComponent>, CustomModelDataComponent.Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 13)
        );

        @Override
        public CustomModelDataComponent create(int customModelData) {
            return new CustomModelDataComponentImpl(customModelData);
        }

        @Override
        public String getName() {
            return "minecraft:custom_model_data";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public CustomModelDataComponent createEmpty() {
            return new CustomModelDataComponentImpl(0);
        }

    }

}
