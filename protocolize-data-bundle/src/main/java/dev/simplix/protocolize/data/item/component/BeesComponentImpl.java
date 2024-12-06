package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.Bee;
import dev.simplix.protocolize.api.item.component.BeesComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class BeesComponentImpl implements BeesComponent {

    private List<Bee> bees;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            Bee bee = new Bee();
            bee.setEntityData((CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
            bee.setTicksInHive(ProtocolUtil.readVarInt(byteBuf));
            bee.setMinTicksInHive(ProtocolUtil.readVarInt(byteBuf));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, bees.size());
        for(Bee bee : bees) {
            NamedBinaryTagUtil.writeTag(byteBuf, bee.getEntityData(), protocolVersion);
            ProtocolUtil.writeVarInt(byteBuf, bee.getTicksInHive());
            ProtocolUtil.writeVarInt(byteBuf, bee.getMinTicksInHive());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addBee(Bee bee) {
        bees.add(bee);
    }

    @Override
    public void removeBee(Bee bee) {
        bees.remove(bee);
    }

    @Override
    public void removeAllBees() {
        bees.clear();
    }

    public static class Type implements StructuredComponentType<BeesComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public BeesComponent create(List<Bee> bees) {
            return new BeesComponentImpl(bees);
        }

        @Override
        public String getName() {
            return "minecraft:bees";
        }


        @Override
        public BeesComponent createEmpty() {
            return new BeesComponentImpl(new ArrayList<>(0));
        }

    }

}
