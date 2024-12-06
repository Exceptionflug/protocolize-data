package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.MaxDamageComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MaxDamageComponentImpl implements MaxDamageComponent {

    private int maxDamage;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        maxDamage = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, maxDamage);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<MaxDamageComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public MaxDamageComponent create(int value) {
            return new MaxDamageComponentImpl(value);
        }

        @Override
        public String getName() {
            return "minecraft:max_damage";
        }

        @Override
        public MaxDamageComponent createEmpty() {
            return new MaxDamageComponentImpl(0);
        }

    }

}
