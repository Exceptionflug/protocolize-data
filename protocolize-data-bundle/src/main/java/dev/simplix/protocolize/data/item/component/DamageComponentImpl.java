package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DamageComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DamageComponentImpl implements DamageComponent {

    private int damage;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        damage = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, damage);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<DamageComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public DamageComponent create(int damage) {
            return new DamageComponentImpl(damage);
        }

        @Override
        public String getName() {
            return "minecraft:damage";
        }

        @Override
        public DamageComponent createEmpty() {
            return new DamageComponentImpl(0);
        }

    }

}
