package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DyedColorComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public static class Type implements StructuredComponentType<DyedColorComponent>, Factory {

        public static Type INSTANCE = new Type();

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
        public DyedColorComponent createEmpty() {
            return new DyedColorComponentImpl(0, true);
        }

    }

}
