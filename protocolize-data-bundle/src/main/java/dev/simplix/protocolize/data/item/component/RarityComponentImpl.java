package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.RarityComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RarityComponentImpl implements RarityComponent {

    private Rarity rarity;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        rarity = Rarity.values()[ProtocolUtil.readVarInt(byteBuf)];
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, rarity.ordinal());
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<RarityComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public RarityComponent create(Rarity rarity) {
            return new RarityComponentImpl(rarity);
        }

        @Override
        public String getName() {
            return "minecraft:rarity";
        }

        @Override
        public RarityComponent createEmpty() {
            return new RarityComponentImpl(Rarity.COMMON);
        }

    }

}
