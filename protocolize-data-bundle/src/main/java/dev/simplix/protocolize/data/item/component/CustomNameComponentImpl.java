package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.CustomNameComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class CustomNameComponentImpl implements CustomNameComponent {

    private ChatElement<?> customName;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        customName = ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        NamedBinaryTagUtil.writeTag(byteBuf, customName.asNbt(), protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<CustomNameComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 5)
        );

        @Override
        public CustomNameComponent create(ChatElement<?> chatElement) {
            return new CustomNameComponentImpl(chatElement);
        }

        @Override
        public String getName() {
            return "minecraft:custom_name";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public CustomNameComponent createEmpty() {
            return new CustomNameComponentImpl(null);
        }

    }

}
