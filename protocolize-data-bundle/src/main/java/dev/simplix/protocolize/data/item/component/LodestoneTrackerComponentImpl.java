package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.BlockPosition;
import dev.simplix.protocolize.api.item.component.LodestoneTrackerComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.BlockPositionSerializer;
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
public class LodestoneTrackerComponentImpl implements LodestoneTrackerComponent {

    private String dimension;
    private BlockPosition position;
    private boolean tracked;


    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(byteBuf.readBoolean()){
            dimension = ProtocolUtil.readString(byteBuf);
            position = BlockPositionSerializer.read(byteBuf, protocolVersion);
        }
        tracked = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeBoolean(dimension != null && position != null);
        if(dimension != null && position != null){
            ProtocolUtil.writeString(byteBuf, dimension);
            BlockPositionSerializer.write(byteBuf, position, protocolVersion);
        }
        byteBuf.writeBoolean(tracked);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<LodestoneTrackerComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 44),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 43)
        );

        @Override
        public LodestoneTrackerComponent create(boolean tracked) {
            return new LodestoneTrackerComponentImpl(null, null, tracked);
        }

        public LodestoneTrackerComponent create(String dimension, BlockPosition position, boolean tracked) {
            return new LodestoneTrackerComponentImpl(dimension, position, tracked);
        }

        @Override
        public String getName() {
            return "minecraft:lodestone_tracker";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public LodestoneTrackerComponent createEmpty() {
            return new LodestoneTrackerComponentImpl(null, null, true);
        }

    }

}
