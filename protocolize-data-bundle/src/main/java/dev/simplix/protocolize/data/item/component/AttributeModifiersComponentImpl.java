package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.Attribute;
import dev.simplix.protocolize.api.item.component.AttributeModifiersComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.AttributeType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class AttributeModifiersComponentImpl implements AttributeModifiersComponent {

    private List<Attribute> attributes;
    private boolean showInTooltip;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws IOException {
        int attributeCount = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < attributeCount; i++){
            Attribute attribute = new Attribute();
            attribute.setType(MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, AttributeType.class));
            if(protocolVersion < MINECRAFT_1_21){
                attribute.setUuid(ProtocolUtil.readUniqueId(byteBuf));
            }
            attribute.setName(ProtocolUtil.readString(byteBuf));
            attribute.setValue(byteBuf.readDouble());
            attribute.setOperation(Attribute.Operation.values()[ProtocolUtil.readVarInt(byteBuf)]);
            attribute.setSlot(Attribute.EquipmentSlot.values()[ProtocolUtil.readVarInt(byteBuf)]);
            attributes.add(attribute);
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws IOException {
        ProtocolUtil.writeVarInt(byteBuf, attributes.size());
        for(Attribute attribute : attributes){
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(attribute.getType(), protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", attribute.getType().name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
            if(protocolVersion < MINECRAFT_1_21){
                ProtocolUtil.writeUniqueId(byteBuf, attribute.getUuid());
            }
            ProtocolUtil.writeString(byteBuf, attribute.getName());
            byteBuf.writeDouble(attribute.getValue());
            ProtocolUtil.writeVarInt(byteBuf, attribute.getOperation().ordinal());
            ProtocolUtil.writeVarInt(byteBuf, attribute.getSlot().ordinal());
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    @Override
    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    @Override
    public void removeAllAttributes() {
        attributes.clear();
    }

    public static class Type implements StructuredComponentType<AttributeModifiersComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 12)
        );

        @Override
        public String getName() {
            return "minecraft:attribute_modifiers";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public AttributeModifiersComponent createEmpty() {
            return new AttributeModifiersComponentImpl(new ArrayList<>(), true);
        }

        @Override
        public AttributeModifiersComponent create(List<Attribute> attributes) {
            return new AttributeModifiersComponentImpl(attributes, true);
        }

        @Override
        public AttributeModifiersComponent create(List<Attribute> attributes, boolean showInTooltip) {
            return new AttributeModifiersComponentImpl(attributes, showInTooltip);
        }
    }

}
