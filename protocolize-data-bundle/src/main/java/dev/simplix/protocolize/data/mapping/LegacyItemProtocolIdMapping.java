package dev.simplix.protocolize.data.mapping;

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
public class LegacyItemProtocolIdMapping extends AbstractProtocolMapping implements ProtocolIdMapping {

    private final int id;
    private final short data;

    public LegacyItemProtocolIdMapping(int protocolRangeStart, int protocolRangeEnd, int id, short data) {
        super(protocolRangeStart, protocolRangeEnd);
        this.id = id;
        this.data = data;
    }

}
