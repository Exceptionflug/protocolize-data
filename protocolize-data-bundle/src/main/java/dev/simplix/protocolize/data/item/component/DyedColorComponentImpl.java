package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DyedColorComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class DyedColorComponentImpl implements DyedColorComponent {

    private int color;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        color = byteBuf.readInt();
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeInt(color);
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<DyedColorComponent>, DyedColorComponent.Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 24)
        );

        @Override
        public DyedColorComponent create(int color) {
            return new DyedColorComponentImpl(color, true);
        }

        @Override
        public DyedColorComponent create(int color, boolean showInTooltip) {
            return new DyedColorComponentImpl(color, showInTooltip);
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
        public DyedColorComponent createEmpty() {
            return new DyedColorComponentImpl(0, true);
        }

    }

}
