package dev.simplix.protocolize.data.inventory;


import dev.simplix.protocolize.data.mapping.InventoryProtocolIdMapping;
import lombok.extern.slf4j.Slf4j;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

@Slf4j
public enum InventoryType {

    GENERIC_9X1(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 9), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 0, 9)),
    GENERIC_9X2(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 18), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 1, 18)),
    GENERIC_9X3(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 27), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 2, 27)),
    GENERIC_9X4(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 36), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 3, 36)),
    GENERIC_9X5(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 45), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 4, 45)),
    GENERIC_9X6(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:container", 54), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 5, 54)),
    GENERIC_3X3(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:dropper", 9), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 6, 9)),
    LEGACY_DISPENSER(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:dispenser", 9), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 6, 9)),
    LEGACY_CHEST_9X1(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:chest", 9)),
    LEGACY_CHEST_9X2(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:chest", 18)),
    LEGACY_CHEST_9X3(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:chest", 27)),
    LEGACY_CHEST_9X4(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:chest", 36)),
    LEGACY_CHEST_9X5(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:chest", 45)),
    LEGACY_CHEST_9X6(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:chest", 54)),
    ANVIL(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:anvil", 3), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 7, 3)),
    BEACON(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:beacon", 1), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 8, 1)),
    BLAST_FURNACE(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 9, 2)),
    BREWING_STAND(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, "minecraft:brewing_stand", 4), new InventoryProtocolIdMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, "minecraft:brewing_stand", 5), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 10, 5)),
    CRAFTING(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:crafting_table", 10), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 11, 10)),
    ENCHANTMENT(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:enchanting_table", 2), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 12, 2)),
    FURNACE(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:furnace", 3), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 13, 3)),
    GRINDSTONE(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 14, 3)),
    HOPPER(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:hopper", 5), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 15, 5)),
    LECTERN(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 16, 0)),
    LOOM(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 17, 4)),
    MERCHANT(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_13_2, "minecraft:villager", 3), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 18, 3)),
    SHULKER_BOX(new InventoryProtocolIdMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, "minecraft:shulker_box", 27), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 19, 27)),
    SMITHING(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 20, 3)),
    SMOKER(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, 21, 3)),
    CARTOGRAPHY(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_1_19_3, 22, 3), new InventoryProtocolIdMapping(MINECRAFT_1_19_4, MINECRAFT_LATEST, 23, 3)),
    STONECUTTER(new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_1_19_3, 23, 2), new InventoryProtocolIdMapping(MINECRAFT_1_19_4, MINECRAFT_LATEST, 24, 2)),
    PLAYER(new InventoryProtocolIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, "Player", 45), new InventoryProtocolIdMapping(MINECRAFT_1_9, MINECRAFT_1_13_2, "Player", 46), new InventoryProtocolIdMapping(MINECRAFT_1_14, MINECRAFT_LATEST, -1, 46));

    private final InventoryProtocolIdMapping[] mappings;

    InventoryType(final InventoryProtocolIdMapping... mappings) {
        this.mappings = mappings;
    }

    public static InventoryType type(final String id, final int size, final int protocolVersion) {
        for (final InventoryType type : values()) {
            if (protocolVersion >= MINECRAFT_1_14) {
                throw new UnsupportedOperationException("Please use InventoryType#getType(id: int, protocolVersion: int): InventoryType for 1.14 protocol version.");
            }
            final String typeId = type.legacyTypeId(protocolVersion);
            if (typeId == null || !typeId.equals(id)) {
                continue;
            }
            if (type.shouldInventorySizeNotBeZero() && type.getTypicalSize(protocolVersion) != size) {
                continue;
            }
            return type;
        }
        return null;
    }

    public static InventoryType type(final int id, final int protocolVersion) {
        for (final InventoryType type : values()) {
            if (protocolVersion < MINECRAFT_1_14) {
                throw new UnsupportedOperationException("Please use InventoryType#getType(id: String, protocolVersion: int): InventoryType for legacy protocol versions.");
            }
            final int typeId = type.getTypeId(protocolVersion);
            if (typeId != -1 && typeId == id)
                return type;
        }
        return null;
    }

    public static InventoryType chestInventoryWithSize(int size) {
        if (size % 9 != 0) {
            throw new IllegalArgumentException("Size must be dividable by 9");
        }
        final int rows = size / 9;
        return chestInventoryWithRows(rows);
    }

    public static InventoryType chestInventoryWithRows(int rows) {
        return valueOf("GENERIC_9X" + rows);
    }

    public InventoryProtocolIdMapping[] mappings() {
        return mappings;
    }

    public String legacyTypeId(int protocolVersion) {
        for (final InventoryProtocolIdMapping mapping : mappings) {
            if (mapping.protocolRangeStart() <= protocolVersion && mapping.protocolRangeEnd() >= protocolVersion) {
                return mapping.legacyId();
            }
        }
        return null;
    }

    public int getTypeId(final int protocolVersion) {
        for (final InventoryProtocolIdMapping mapping : mappings) {
            if (mapping.protocolRangeStart() <= protocolVersion && mapping.protocolRangeEnd() >= protocolVersion) {
                return mapping.id();
            }
        }
        return -1;
    }

    public int getTypicalSize(final int protocolVersion) {
        for (final InventoryProtocolIdMapping mapping : mappings) {
            if (mapping.protocolRangeStart() <= protocolVersion && mapping.protocolRangeEnd() >= protocolVersion) {
                return mapping.typicalSize();
            }
        }
        log.warn("[Protocolize] Unable to find typical inventory size of " + name() + " in version " + protocolVersion);
        return -1;
    }

    public boolean shouldInventorySizeNotBeZero() {
        return this != CRAFTING && this != ENCHANTMENT && this != ANVIL;
    }

}
