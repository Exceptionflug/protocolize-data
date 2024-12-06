package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.ItemNameComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemNameComponentImpl implements ItemNameComponent {

    private ChatElement<?> name;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        name = ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        NamedBinaryTagUtil.writeTag(byteBuf, name.asNbt(), protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<ItemNameComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public ItemNameComponent create(ChatElement<?> chatElement) {
            return new ItemNameComponentImpl(chatElement);
        }

        @Override
        public String getName() {
            return "minecraft:item_name";
        }

        @Override
        public ItemNameComponent createEmpty() {
            return new ItemNameComponentImpl(null);
        }

    }

}
