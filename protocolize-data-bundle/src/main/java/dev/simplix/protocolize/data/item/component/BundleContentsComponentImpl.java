package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.item.ItemStackSerializer;
import dev.simplix.protocolize.api.item.component.BundleContentsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BundleContentsComponentImpl implements BundleContentsComponent {

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

    public static class Type implements StructuredComponentType<BundleContentsComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public BundleContentsComponent create(List<BaseItemStack> items) {
            return new BundleContentsComponentImpl(items);
        }

        @Override
        public String getName() {
            return "minecraft:bundle_contents";
        }

        @Override
        public BundleContentsComponent createEmpty() {
            return new BundleContentsComponentImpl(new ArrayList<>(0));
        }

    }

}
