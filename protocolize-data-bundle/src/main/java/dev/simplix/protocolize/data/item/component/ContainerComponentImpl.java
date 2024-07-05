package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.item.component.ContainerComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class ContainerComponentImpl implements ContainerComponent {

    private List<BaseItemStack> items;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            items.add(ItemStackSerializer.read(byteBuf, protocolVersion));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, items.size());
        for(BaseItemStack item : items) {
            ItemStackSerializer.write(byteBuf, item, protocolVersion);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addItem(BaseItemStack itemStack) {
        items.add(itemStack);
    }

    @Override
    public void removeItem(BaseItemStack itemStack) {
        items.remove(itemStack);
    }

    @Override
    public void removeAllItems() {
        items.clear();
    }

    public static class Type implements StructuredComponentType<ContainerComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 52)
        );

        @Override
        public ContainerComponent create(List<BaseItemStack> items) {
            return new ContainerComponentImpl(items);
        }

        @Override
        public String getName() {
            return "minecraft:charged_projectiles";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public ContainerComponent createEmpty() {
            return new ContainerComponentImpl(new ArrayList<>(0));
        }

    }

}
