package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MaxStackSizeComponent;
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
public class MaxStackSizeComponentImpl implements MaxStackSizeComponent {

    private int maxStackSize;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        maxStackSize = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, maxStackSize);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<MaxStackSizeComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 1)
        );

        @Override
        public MaxStackSizeComponent create(int maxStackSize) {
            return new MaxStackSizeComponentImpl(maxStackSize);
        }

        @Override
        public String getName() {
            return "minecraft:max_stack_size";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public MaxStackSizeComponent createEmpty() {
            return new MaxStackSizeComponentImpl(0);
        }

    }

}
