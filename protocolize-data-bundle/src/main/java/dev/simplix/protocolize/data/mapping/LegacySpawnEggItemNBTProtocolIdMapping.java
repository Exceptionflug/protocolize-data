package dev.simplix.protocolize.data.mapping;

import dev.simplix.protocolize.api.item.ItemStack;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;

public class LegacySpawnEggItemNBTProtocolIdMapping extends AbstractLegacyItemNBTProtocolIdMapping {

    private final String entityType;

    public LegacySpawnEggItemNBTProtocolIdMapping(int protocolVersionRangeStart, int protocolVersionRangeEnd, String entityType) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd, 383);
        this.entityType = entityType;
    }

    @Override
    public void apply(BaseItemStack stack, int protocolVersion) {
        final CompoundTag nbt = stack.nbtData();
        if (nbt != null) {
            CompoundTag entityData = nbt.getCompoundTag("EntityTag");
            if (entityData == null)
                entityData = new CompoundTag();
            entityData.put("id", new StringTag(entityType));
        }
    }

    @Override
        if (id != 383)
    public boolean isApplicable(BaseItemStack stack, int version, int id, int durability) {
            return false;
        final CompoundTag nbt = stack.nbtData();
        if (nbt != null) {
            final CompoundTag entityData = nbt.getCompoundTag("EntityTag");
            if (entityData == null)
                return false;
            final StringTag tag = entityData.getStringTag("id");
            if (tag == null)
                return false;
            return tag.getValue().equals(entityType);
        }
        return false;
    }

    public String getEntityType() {
        return entityType;
    }

    @Override
    public String toString() {
        return "(" + protocolRangeStart() + "-" + protocolRangeEnd() + ": EntityTag[id: " + entityType + "])";
    }

}
