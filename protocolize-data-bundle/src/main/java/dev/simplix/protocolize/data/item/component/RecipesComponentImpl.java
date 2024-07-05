package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.RecipesComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class RecipesComponentImpl implements RecipesComponent {

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

    public static class Type implements StructuredComponentType<RecipesComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 43),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 42)
        );

        @Override
        public String getName() {
            return "minecraft:recipes";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public RecipesComponent createEmpty() {
            return new RecipesComponentImpl(null);
        }

        @Override
        public RecipesComponent create(CompoundTag compoundTag) {
            return new RecipesComponentImpl(compoundTag);
        }
    }

}
