package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.RecipesComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.io.IOException;

@Data
@AllArgsConstructor
public class RecipesComponentImpl implements RecipesComponent {

    /* This is a JSON list of ResourceLocation's. Can be looked at, at a later date. */
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

        @Override
        public String getName() {
            return "minecraft:recipes";
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
