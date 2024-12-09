package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.RepairableComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class RepairableComponentImpl implements RepairableComponent {

    private Either<String, List<ItemType>> items;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        items = StructuredComponentUtil.readHolderSet(byteBuf, ItemType.class, protocolVersion);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        StructuredComponentUtil.writeHolderSet(byteBuf, items, ItemType.class, protocolVersion);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<RepairableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public RepairableComponent create(List<ItemType> items) {
            return new RepairableComponentImpl(Either.right(items));
        }

        @Override
        public RepairableComponent create(String itemResourceLocation) {
            return new RepairableComponentImpl(Either.left(itemResourceLocation));
        }

        @Override
        public String getName() {
            return "minecraft:repairable";
        }

        @Override
        public RepairableComponent createEmpty() {
            return new RepairableComponentImpl(Either.right(new ArrayList<>(0)));
        }

    }
}
