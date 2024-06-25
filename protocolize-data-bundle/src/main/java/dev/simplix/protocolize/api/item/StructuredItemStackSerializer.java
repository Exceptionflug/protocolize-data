package dev.simplix.protocolize.api.item;

import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.component.*;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.ItemType;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "Protocolize")
public final class StructuredItemStackSerializer {

    private static final List<Integer> UNKNOWN_ITEMS = new ArrayList<>();
    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    public ItemStack read(ByteBuf buf, int protocolVersion) {
        int amount = ProtocolUtil.readVarInt(buf);
        if (amount == 0) {
            return ItemStack.NO_DATA;
        }
        int itemId = ProtocolUtil.readVarInt(buf);
        ItemType type = findItemType(itemId, protocolVersion);
        if (type == null && !UNKNOWN_ITEMS.contains(itemId)) { //prevent console spam by checking if already logged
            UNKNOWN_ITEMS.add(itemId);
            log.warn("Don't know what item {} at protocol {} should be.", itemId, protocolVersion);
        }
        int toAdd = ProtocolUtil.readVarInt(buf);
        int toRemove = ProtocolUtil.readVarInt(buf);
        List<StructuredComponent> componentsToAdd = new ArrayList<>(toAdd);
        List<StructuredComponentType<?>> componentsToRemove = new ArrayList<>(toRemove);
        for (int i = 0; i < toAdd; i++) {
            componentsToAdd.add(readComponent(buf, protocolVersion));
        }
        for (int i = 0; i < toRemove; i++) {
            componentsToRemove.add(readComponentType(buf, protocolVersion));
        }
        ItemStack out = new ItemStack(type, amount);
        componentsToAdd.forEach(out::addComponent);
        componentsToRemove.forEach(out::removeComponent);
        populateLoreAndDisplayNameFromComponents(out);
        return out;
    }

    public void write(ByteBuf buf, BaseItemStack stack, int protocolVersion) {
        if (stack.itemType() == null) {
            ProtocolUtil.writeVarInt(buf, 0);
            return;
        }
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(stack.itemType(), protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            log.warn("{} cannot be used on protocol version {}", stack.itemType().name(), protocolVersion);
            ProtocolUtil.writeVarInt(buf, 0);
            return;
        }
        ProtocolUtil.writeVarInt(buf, stack.amount());
        if (stack.amount() == 0) {
            return;
        }
        populateComponentsFromDisplayNameAndLore(stack);
        ProtocolUtil.writeVarInt(buf, ((ProtocolIdMapping) mapping).id());
        ProtocolUtil.writeVarInt(buf, stack.getComponents().size());
        ProtocolUtil.writeVarInt(buf, stack.getComponentsToRemove().size());
        for (StructuredComponent component : stack.getComponents()) {
            writeComponent(buf, component, protocolVersion);
        }
        for (StructuredComponentType<?> type : stack.getComponentsToRemove()) {
            writeComponentType(buf, type, protocolVersion);
        }
    }

    private void populateComponentsFromDisplayNameAndLore(BaseItemStack stack) {
        if (stack.displayName() != null) {
            stack.addComponent(CustomNameComponent.create(stack.displayName().disableItalic()));
        }
        if (stack.lore() != null && !stack.lore().isEmpty()) {
            stack.addComponent(LoreComponent.create(stack.lore().stream().
                map(ChatElement::disableItalic)
                .collect(Collectors.toList())));
        }
    }

    private void writeComponent(ByteBuf buf, StructuredComponent component, int protocolVersion) {
        writeComponentType(buf, component.getType(), protocolVersion);
        try {
            component.write(buf, protocolVersion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to write " + component.getClass().getSimpleName() + " of item", e);
        }
    }

    private void writeComponentType(ByteBuf buf, StructuredComponentType<?> componentType, int protocolVersion) {
        ProtocolMapping mapping = MAPPING_PROVIDER.mapping(componentType, protocolVersion);
        if (!(mapping instanceof ProtocolIdMapping)) {
            throw new IllegalStateException("Component " + componentType.getName() + " can not be used on protocol version " + protocolVersion);
        }
        ProtocolUtil.writeVarInt(buf, ((ProtocolIdMapping) mapping).id());
    }

    private void populateLoreAndDisplayNameFromComponents(ItemStack itemStack) {
        ItemNameComponent itemNameComponent = itemStack.getComponent(ItemNameComponent.class);
        if (itemNameComponent != null) {
            itemStack.displayName(itemNameComponent.getName());
        }
        CustomNameComponent customNameComponent = itemStack.getComponent(CustomNameComponent.class);
        if (customNameComponent != null) {
            itemStack.displayName(customNameComponent.getCustomName());
        }
        LoreComponent loreComponent = itemStack.getComponent(LoreComponent.class);
        if (loreComponent != null) {
            itemStack.lore(loreComponent.getLore());
        }
    }

    private StructuredComponent readComponent(ByteBuf buf, int protocolVersion) {
        StructuredComponentType<?> type = readComponentType(buf, protocolVersion);
        StructuredComponent component = type.createEmpty();
        try {
            component.read(buf, protocolVersion);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read " + component.getClass().getSimpleName() + " from item", e);
        }
        return component;
    }

    private StructuredComponentType<?> readComponentType(ByteBuf buf, int protocolVersion) {
        int componentId = ProtocolUtil.readVarInt(buf);
        return findComponentType(componentId, protocolVersion);
    }

    private StructuredComponentType<?> findComponentType(int componentId, int protocolVersion) {
        Multimap<StructuredComponentType, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(StructuredComponentType.class, protocolVersion);
        for (StructuredComponentType<?> type : mappings.keySet()) {
            for (ProtocolMapping mapping : mappings.get(type)) {
                if (mapping instanceof ProtocolIdMapping && ((ProtocolIdMapping) mapping).id() == componentId) {
                    return type;
                }
            }
        }
        throw new IllegalStateException("Could not find component type " + componentId + " for protocol version " +
            protocolVersion + ". This item is not compatible with Protocolize.");
    }

    private ItemType findItemType(int id, int protocolVersion) {
        Multimap<ItemType, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(ItemType.class, protocolVersion);
        for (ItemType type : mappings.keySet()) {
            for (ProtocolMapping mapping : mappings.get(type)) {
                if (mapping instanceof ProtocolIdMapping) {
                    if (((ProtocolIdMapping) mapping).id() == id) {
                        return type;
                    }
                }
            }
        }
        return null;
    }

}
