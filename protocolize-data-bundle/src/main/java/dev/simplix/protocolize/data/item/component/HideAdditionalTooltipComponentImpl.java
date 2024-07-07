package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.HideAdvancedTooltipComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

public class HideAdditionalTooltipComponentImpl implements HideAdvancedTooltipComponent {

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

    public static class Type implements StructuredComponentType<HideAdvancedTooltipComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 14)
        );

        @Override
        public HideAdvancedTooltipComponent create() {
            return new HideAdditionalTooltipComponentImpl();
        }

        @Override
        public String getName() {
            return "minecraft:hide_additional_tooltip";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public HideAdvancedTooltipComponent createEmpty() {
            return create();
        }

    }

}
