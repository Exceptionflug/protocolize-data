package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.ProfileComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.Property;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class ProfileComponentImpl implements ProfileComponent {

    private String name;
    private UUID uniqueId;
    private List<Property> properties;

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
        if (byteBuf.readBoolean()) {
            name = ProtocolUtil.readString(byteBuf);
        }
        if (byteBuf.readBoolean()) {
            uniqueId = ProtocolUtil.readUniqueId(byteBuf);
        }
        int propertiesSize = ProtocolUtil.readVarInt(byteBuf);
        properties = new ArrayList<>(propertiesSize);
        for (int j = 0; j < propertiesSize; j++) {
            String name = ProtocolUtil.readString(byteBuf);
            String value = ProtocolUtil.readString(byteBuf);
            String signature = byteBuf.readBoolean() ? ProtocolUtil.readString(byteBuf) : null;
            properties.add(new Property(name, value, signature));
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
        byteBuf.writeBoolean(name != null);
        if (name != null) {
            ProtocolUtil.writeString(byteBuf, name);
        }
        byteBuf.writeBoolean(uniqueId != null);
        if (uniqueId != null) {
            ProtocolUtil.writeUniqueId(byteBuf, uniqueId);
        }
        ProtocolUtil.writeVarInt(byteBuf, properties.size());
        for (Property property : properties) {
            ProtocolUtil.writeString(byteBuf, property.getName());
            ProtocolUtil.writeString(byteBuf, property.getValue());
            byteBuf.writeBoolean(property.getSignature() != null);
            if (property.getSignature() != null) {
                ProtocolUtil.writeString(byteBuf, property.getSignature());
            }
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<ProfileComponent>, ProfileComponent.Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 47),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 46)
        );

        @Override
        public ProfileComponent create(String name, UUID uniqueId, List<Property> properties) {
            return new ProfileComponentImpl(name, uniqueId, properties);
        }

        @Override
        public String getName() {
            return "minecraft:profile";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public ProfileComponent createEmpty() {
            return new ProfileComponentImpl(null, null, null);
        }

    }

}
