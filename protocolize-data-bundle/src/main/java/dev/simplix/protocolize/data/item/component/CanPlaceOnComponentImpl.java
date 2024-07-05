package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.BlockPredicate;
import dev.simplix.protocolize.api.item.component.CanPlaceOnComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.StructureComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class CanPlaceOnComponentImpl implements CanPlaceOnComponent {

    private List<BlockPredicate> blockPredicates;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            blockPredicates.add(StructureComponentUtil.readBlockPredicate(byteBuf, protocolVersion));
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, blockPredicates.size());
        for(BlockPredicate blockPredicate : blockPredicates) {
            StructureComponentUtil.writeBlockPredicate(byteBuf, blockPredicate, protocolVersion);
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<CanPlaceOnComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 10)
        );

        @Override
        public CanPlaceOnComponent create(List<BlockPredicate> blockPredicates, boolean showInTooltip) {
            return new CanPlaceOnComponentImpl(blockPredicates, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:can_place_on";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public CanPlaceOnComponent createEmpty() {
            return new CanPlaceOnComponentImpl(new ArrayList<>(0), true);
        }

    }

}
