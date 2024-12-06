package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.HideTooltipComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import io.netty.buffer.ByteBuf;

public class HideTooltipComponentImpl implements HideTooltipComponent {

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<HideTooltipComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public HideTooltipComponent create() {
            return new HideTooltipComponentImpl();
        }

        @Override
        public String getName() {
            return "minecraft:hide_tooltip";
        }

        @Override
        public HideTooltipComponent createEmpty() {
            return create();
        }

    }

}
