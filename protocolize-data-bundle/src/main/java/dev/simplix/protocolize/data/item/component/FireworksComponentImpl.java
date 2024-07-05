package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.Firework;
import dev.simplix.protocolize.api.item.component.FireworksComponent;
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
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class FireworksComponentImpl implements FireworksComponent {

    private Firework firework;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        firework.setFlightDuration(ProtocolUtil.readVarInt(byteBuf));
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            firework.getExplosions().add(StructureComponentUtil.readFireworkMeta(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, firework.getFlightDuration());
        ProtocolUtil.writeVarInt(byteBuf, firework.getExplosions().size());
        for(Firework.Meta meta : firework.getExplosions()) {
            StructureComponentUtil.writeFireworkMeta(byteBuf, meta, protocolVersion);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<FireworksComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 46),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 45)
        );

        @Override
        public FireworksComponent create(Firework firework) {
            return new FireworksComponentImpl(firework);
        }

        @Override
        public String getName() {
            return "minecraft:fireworks";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public FireworksComponent createEmpty() {
            return new FireworksComponentImpl(new Firework(0, new ArrayList<>(0)));
        }

    }

}
