package dev.simplix.protocolize.data.mapping;

import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public abstract class AbstractLegacyItemNBTProtocolIdMapping extends AbstractProtocolMapping implements ProtocolIdMapping {

    private final int id;

    public AbstractLegacyItemNBTProtocolIdMapping(int protocolRangeStart, int protocolRangeEnd, int id) {
        super(protocolRangeStart, protocolRangeEnd);
        this.id = id;
    }

    public abstract void apply(ItemStack stack, int protocolVersion);

    public abstract boolean isApplicable(ItemStack stack, int version, int id, int durability);

}
