package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.PotDecorationsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class PotDecorationsComponentImpl implements PotDecorationsComponent {

    /* This should be limited to a size of 4 elements */
    private List<ItemType> decorations;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for(int i = 0; i < count; i++) {
            ItemType itemType = MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, ItemType.class);
            decorations.add(itemType);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, decorations.size());
        for(ItemType decoration : decorations) {
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(decoration, protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                StructuredComponentUtil.logMappingWarning(decoration.name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 0);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id());
            }
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<PotDecorationsComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public PotDecorationsComponent create(List<ItemType> decorations) {
            return new PotDecorationsComponentImpl(decorations);
        }

        @Override
        public String getName() {
            return "minecraft:pot_decorations";
        }

        @Override
        public PotDecorationsComponent createEmpty() {
            return new PotDecorationsComponentImpl(new ArrayList<>(0));
        }

    }

}
