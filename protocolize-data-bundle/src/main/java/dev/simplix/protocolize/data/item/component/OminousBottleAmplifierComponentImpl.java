package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.OminousBottleAmplifierComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OminousBottleAmplifierComponentImpl implements OminousBottleAmplifierComponent {

    private int amplifier;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        amplifier = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, amplifier);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<OminousBottleAmplifierComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public OminousBottleAmplifierComponent create(int id) {
            return new OminousBottleAmplifierComponentImpl(id);
        }

        @Override
        public String getName() {
            return "minecraft:ominous_bottle_amplifier";
        }

        @Override
        public OminousBottleAmplifierComponent createEmpty() {
            return new OminousBottleAmplifierComponentImpl(0);
        }

    }

}
