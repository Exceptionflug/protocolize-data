package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.ItemNameComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

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

    public static class Type implements StructuredComponentType<ItemNameComponent>, ItemNameComponent.Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 6)
        );

        @Override
        public ItemNameComponent create(ChatElement<?> chatElement) {
            return new ItemNameComponentImpl(chatElement);
        }

        @Override
        public String getName() {
            return "minecraft:item_name";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public ItemNameComponent createEmpty() {
            return new ItemNameComponentImpl(null);
        }

    }

}
