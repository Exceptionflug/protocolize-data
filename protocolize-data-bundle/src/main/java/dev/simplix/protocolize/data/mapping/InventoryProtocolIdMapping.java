package dev.simplix.protocolize.data.mapping;

import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;

public class InventoryProtocolIdMapping extends AbstractProtocolMapping implements ProtocolIdMapping {

    private final String legacyId;
    private final int protocolId, typicalSize;

    public InventoryProtocolIdMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final String legacyId, final int typicalSize) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd);
        this.legacyId = legacyId;
        this.typicalSize = typicalSize;
        protocolId = -1;
    }

    public InventoryProtocolIdMapping(final int protocolVersionRangeStart, final int protocolVersionRangeEnd, final int protocolId, final int typicalSize) {
        super(protocolVersionRangeStart, protocolVersionRangeEnd);
        this.protocolId = protocolId;
        this.typicalSize = typicalSize;
        legacyId = null;
    }

    public String legacyId() {
        return legacyId;
    }

    public int typicalSize() {
        return typicalSize;
    }

    @Override
    public int id() {
        return protocolId;
    }
}
