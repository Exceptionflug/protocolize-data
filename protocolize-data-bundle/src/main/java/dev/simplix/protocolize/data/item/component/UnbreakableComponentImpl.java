package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.UnbreakableComponent;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnbreakableComponentImpl implements UnbreakableComponent {

    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<UnbreakableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public UnbreakableComponent create(boolean showInTooltip) {
            return new UnbreakableComponentImpl(showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:unbreakable";
        }

        @Override
        public UnbreakableComponent createEmpty() {
            return new UnbreakableComponentImpl(true);
        }

    }

}
