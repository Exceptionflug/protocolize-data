package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CustomDataComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

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

    public static class Type implements StructuredComponentType<CustomDataComponent>, CustomDataComponent.Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 0)
        );

        @Override
        public String getName() {
            return "minecraft:custom_data";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
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
