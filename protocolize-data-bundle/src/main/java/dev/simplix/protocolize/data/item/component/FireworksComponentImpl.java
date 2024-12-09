package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.Firework;
import dev.simplix.protocolize.api.item.component.FireworksComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class FireworksComponentImpl implements FireworksComponent {

    private Firework firework;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        firework.setFlightDuration(ProtocolUtil.readVarInt(byteBuf));
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            firework.getExplosions().add(StructuredComponentUtil.readFireworkMeta(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, firework.getFlightDuration());
        ProtocolUtil.writeVarInt(byteBuf, firework.getExplosions().size());
        for(Firework.Meta meta : firework.getExplosions()) {
            StructuredComponentUtil.writeFireworkMeta(byteBuf, meta, protocolVersion);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<FireworksComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public FireworksComponent create(Firework firework) {
            return new FireworksComponentImpl(firework);
        }

        @Override
        public String getName() {
            return "minecraft:fireworks";
        }

        @Override
        public FireworksComponent createEmpty() {
            return new FireworksComponentImpl(new Firework(0, new ArrayList<>(0)));
        }

    }

}
