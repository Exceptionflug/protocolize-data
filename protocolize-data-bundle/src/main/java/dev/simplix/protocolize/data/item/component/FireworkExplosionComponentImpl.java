package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.Firework;
import dev.simplix.protocolize.api.item.component.FireworkExplosionComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
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
public class FireworkExplosionComponentImpl implements FireworkExplosionComponent {

    private Firework.Meta explosion;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        explosion = StructureComponentUtil.readFireworkMeta(byteBuf, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        StructureComponentUtil.writeFireworkMeta(byteBuf, explosion, protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<FireworkExplosionComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 45),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 44)
        );

        @Override
        public FireworkExplosionComponent create(Firework.Meta explosion) {
            return new FireworkExplosionComponentImpl(explosion);
        }

        @Override
        public String getName() {
            return "minecraft:firework_explosion";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public FireworkExplosionComponent createEmpty() {
            return new FireworkExplosionComponentImpl(new Firework.Meta(Firework.Meta.Shape.SMALL_BALL, new ArrayList<>(0), new ArrayList<>(0), false, false));
        }

    }

}
