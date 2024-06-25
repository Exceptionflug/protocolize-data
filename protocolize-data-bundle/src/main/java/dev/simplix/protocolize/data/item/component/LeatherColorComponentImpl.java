package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DamageComponent;
import dev.simplix.protocolize.api.item.component.LeatherColorComponent;
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
public class LeatherColorComponentImpl implements LeatherColorComponent {

    private int color;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        color = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, color);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<LeatherColorComponent>, LeatherColorComponent.Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 24)
        );

        @Override
        public LeatherColorComponent create(int color) {
            return new LeatherColorComponentImpl(color);
        }

        @Override
        public String getName() {
            return "minecraft:dyed_color";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public LeatherColorComponent createEmpty() {
            return new LeatherColorComponentImpl(0);
        }

    }

}
