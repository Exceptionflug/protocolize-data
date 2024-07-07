package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.LockComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.StringTag;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class LockComponentImpl implements LockComponent {

    private StringTag key;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        key = (StringTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        NamedBinaryTagUtil.writeTag(byteBuf, key, protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<LockComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 55),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 54)
        );

        @Override
        public LockComponent create(StringTag key) {
            return new LockComponentImpl(key);
        }


        @Override
        public String getName() {
            return "minecraft:lock";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public LockComponent createEmpty() {
            return new LockComponentImpl(null);
        }

    }

}
