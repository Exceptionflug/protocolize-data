package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.DamageResistantComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DamageResistantComponentImpl implements DamageResistantComponent {

    private String types;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        types = ProtocolUtil.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeString(byteBuf, types);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<DamageResistantComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public DamageResistantComponent create(String types) {
            return new DamageResistantComponentImpl(types);
        }

        @Override
        public String getName() {
            return "minecraft:damage_resistant";
        }

        @Override
        public DamageResistantComponent createEmpty() {
            return new DamageResistantComponentImpl("");
        }

    }


}
