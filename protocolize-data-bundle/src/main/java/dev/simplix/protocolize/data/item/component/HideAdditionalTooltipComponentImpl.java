package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.HideAdditionalTooltipComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import io.netty.buffer.ByteBuf;

public class HideAdditionalTooltipComponentImpl implements HideAdditionalTooltipComponent {

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

    public static class Type implements StructuredComponentType<HideAdditionalTooltipComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public HideAdditionalTooltipComponent create() {
            return new HideAdditionalTooltipComponentImpl();
        }

        @Override
        public String getName() {
            return "minecraft:hide_additional_tooltip";
        }

        @Override
        public HideAdditionalTooltipComponent createEmpty() {
            return create();
        }

    }

}
