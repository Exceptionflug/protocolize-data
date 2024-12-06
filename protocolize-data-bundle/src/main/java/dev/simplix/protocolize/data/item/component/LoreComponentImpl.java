package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.LoreComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class LoreComponentImpl implements LoreComponent {

    private List<ChatElement<?>> lore;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int lines = ProtocolUtil.readVarInt(byteBuf);
        lore = new ArrayList<>(lines);
        for (int i = 0; i < lines; i++) {
            lore.add(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, lore.size());
        for (ChatElement<?> lore : lore) {
            NamedBinaryTagUtil.writeTag(byteBuf, lore.asNbt(), protocolVersion);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<LoreComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public LoreComponent create(List<ChatElement<?>> lore) {
            return new LoreComponentImpl(lore);
        }

        @Override
        public String getName() {
            return "minecraft:lore";
        }

        @Override
        public LoreComponent createEmpty() {
            return new LoreComponentImpl(null);
        }

    }

}
