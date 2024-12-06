package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.RepairCostComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RepairCostComponentImpl implements RepairCostComponent {

    private int cost;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        cost = ProtocolUtil.readVarInt(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, cost);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<RepairCostComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public RepairCostComponent create(int id) {
            return new RepairCostComponentImpl(id);
        }

        @Override
        public String getName() {
            return "minecraft:repair_cost";
        }

        @Override
        public RepairCostComponent createEmpty() {
            return new RepairCostComponentImpl(0);
        }

    }

}
