package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.DyeColor;
import dev.simplix.protocolize.api.item.component.BaseColorComponent;
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
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class BaseColorComponentImpl implements BaseColorComponent {

    private DyeColor color;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        color = DyeColor.values()[ProtocolUtil.readVarInt(byteBuf)];
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, color.ordinal());
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<BaseColorComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 50),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 49)
        );

        @Override
        public BaseColorComponent create(DyeColor color) {
            return new BaseColorComponentImpl(color);
        }


        @Override
        public String getName() {
            return "minecraft:base_color";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public BaseColorComponent createEmpty() {
            return new BaseColorComponentImpl(DyeColor.WHITE);
        }

    }

}
