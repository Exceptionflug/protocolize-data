package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.PotDecorationsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class PotDecorationsComponentImpl implements PotDecorationsComponent {

    private List<Integer> decorations;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            decorations.add(ProtocolUtil.readVarInt(byteBuf));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, decorations.size());
        for(int decoration : decorations) {
            ProtocolUtil.writeVarInt(byteBuf, decoration);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<PotDecorationsComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 51),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 50)
        );

        @Override
        public PotDecorationsComponent create(List<Integer> decorations) {
            return new PotDecorationsComponentImpl(decorations);
        }


        @Override
        public String getName() {
            return "minecraft:pot_decorations";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public PotDecorationsComponent createEmpty() {
            return new PotDecorationsComponentImpl(new ArrayList<>(0));
        }

    }

}
