package dev.simplix.protocolize.api.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import dev.simplix.protocolize.api.ComponentConverter;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.ComponentConverterProvider;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.DebugUtil;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.mapping.AbstractLegacyItemNBTProtocolIdMapping;
import dev.simplix.protocolize.data.mapping.LegacyItemProtocolIdMapping;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
public final class ItemStackSerializer {

    private static final List<Integer> unknownItems = new ArrayList<>();
    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();
    private static final ComponentConverter CONVERTER = Protocolize.getService(ComponentConverterProvider.class)
        .platformConverter();

    private ItemStackSerializer() {
    }

    // READ

    public static ItemStack read(ByteBuf buf, int protocolVersion) {
        Preconditions.checkNotNull(buf, "Buf cannot be null");
        try {
            int id = readItemId(buf, protocolVersion);
            if (id == -1) {
                return ItemStack.NO_DATA;
            }
            byte amount = buf.readByte();
            short durability = 0;
            if (protocolVersion < MINECRAFT_1_13) {
                durability = buf.readShort();
            }
            if (id == 0) {
                return new ItemStack(ItemType.AIR, amount, durability);
            }
            CompoundTag tag = readItemTag(buf, protocolVersion);
            String displayNameJson = null;
            List<String> loreJson = readLoreJson(tag, protocolVersion);
            if (protocolVersion >= MINECRAFT_1_13 && tag != null) {
                IntTag damageTag = tag.getIntTag("Damage");
                if (damageTag != null) {
                    durability = damageTag.asShort();
                }
                displayNameJson = extractDisplayName(tag);
            } else if (tag != null) {
                String displayName = extractDisplayName(tag);
                if (displayName != null) {
                    displayNameJson = CONVERTER.toJson(CONVERTER.fromLegacyText(displayName));
                }
            }
            ItemType type = lookupItemType(id, durability, protocolVersion);
            if (type == null && !unknownItems.contains(id)) { //prevent console spam by checking if already logged
                unknownItems.add(id);
                log.warn("Don't know what item " + id + " at protocol " + protocolVersion + " should be.");
            }
            ItemStack out = new ItemStack(type, amount, durability);
            out.displayNameJson(displayNameJson);
            out.loreJson(loreJson == null ? new ArrayList<>() : loreJson);
            out.nbtData(tag);
            out.hideFlags(readHideFlags(tag));
            return out;
        } catch (Exception e) {
            DebugUtil.writeDump(buf, e);
            log.error("Unable to read item stack from buffer in protocol version " + protocolVersion, e);
            return ItemStack.NO_DATA;
        }
    }

    private static ItemType lookupItemType(int id, short data, int protocolVersion) {
        Multimap<ItemType, ProtocolMapping> mappings = MAPPING_PROVIDER.mappings(ItemType.class, protocolVersion);
        List<ItemType> legacyCandidates = new ArrayList<>();
        for (ItemType type : mappings.keySet()) {
            if (protocolVersion >= MINECRAFT_1_13) {
                for (ProtocolMapping mapping : mappings.get(type)) {
                    if (mapping instanceof ProtocolIdMapping) {
                        if (((ProtocolIdMapping) mapping).id() == id) {
                            return type;
                        }
                    }
                }
            } else {
                for (ProtocolMapping mapping : mappings.get(type)) {
                    if (mapping instanceof LegacyItemProtocolIdMapping) {
                        if (((ProtocolIdMapping) mapping).id() == id) {
                            legacyCandidates.add(type);
                            if (((LegacyItemProtocolIdMapping) mapping).data() == data) {
                                return type;
                            }
                        }
                    }
                }
            }
        }
        // Tools and stuff with durability will fail the above determination logic.
        // So we just validate the id and if we are sure about the fact that this item can only
        // have one possible state, we will return that.
        if (legacyCandidates.size() == 1) {
            return legacyCandidates.get(0);
        }
        return null;
    }

    private static int readHideFlags(CompoundTag tag) {
        if (tag == null)
            return 0;
        if (tag.containsKey("HideFlags")) {
            return ((NumberTag<?>) tag.get("HideFlags")).asInt();
        }
        return 0;
    }

    private static List<String> readLoreJson(CompoundTag tag, int protocolVersion) {
        if (tag == null)
            return null;
        CompoundTag display = tag.getCompoundTag("display");
        if (display == null) {
            return null;
        }
        ListTag<StringTag> lore = (ListTag<StringTag>) display.get("Lore");
        if (lore == null) {
            return null;
        }
        List<StringTag> tags = new ArrayList<>();
        lore.iterator().forEachRemaining(tags::add);
        if (protocolVersion < MINECRAFT_1_14) {
            return tags.stream().map(stringTag -> CONVERTER.toJson(CONVERTER.fromLegacyText(stringTag.getValue()))).collect(Collectors.toList());
        }
        return tags.stream().map(StringTag::getValue).collect(Collectors.toList());
    }

    private static String extractDisplayName(CompoundTag tag) {
        if (tag == null)
            return null;
        CompoundTag display = tag.getCompoundTag("display");
        if (display == null) {
            return null;
        }
        StringTag name = display.getStringTag("Name");
        if (name == null) {
            return null;
        }
        return name.getValue();
    }

    private static CompoundTag readItemTag(ByteBuf byteBuf, int protocolVersion) throws IOException {
        Tag<?> tag = NamedBinaryTagUtil.readTag(byteBuf, protocolVersion);
        if (tag == null) {
            return new CompoundTag();
        }
        return (CompoundTag) tag;
    }

    private static int readItemId(ByteBuf buf, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_13_2) {
            return buf.readShort();
        } else {
            if (buf.readBoolean()) {
                return ProtocolUtil.readVarInt(buf);
            } else {
                return -1;
            }
        }
    }

    // WRITE

    public static void write(ByteBuf buf, BaseItemStack stack, int protocolVersion) {
        Preconditions.checkNotNull(buf, "Buf cannot be null");
        Preconditions.checkNotNull(stack, "Stack cannot be null");
        try {
            int protocolId;
            short durability = 0;
            ProtocolIdMapping mapping = null;
            if (stack.itemType() == null) {
                protocolId = -1;
            } else {
                mapping = MAPPING_PROVIDER.mapping(stack.itemType(), protocolVersion);
                if (mapping == null) {
                    protocolId = -2;
                } else {
                    protocolId = mapping.id();
                    if (mapping instanceof LegacyItemProtocolIdMapping) {
                        durability = ((LegacyItemProtocolIdMapping) mapping).data();
                    }
                }
            }
            if (protocolId == -2) {
                if (protocolVersion < MINECRAFT_1_13_2) {
                    buf.writeShort(-1);
                } else {
                    buf.writeBoolean(false);
                }
                log.warn(stack.itemType().name() + " cannot be used on protocol version " + protocolVersion);
                return;
            }
            if (protocolVersion < MINECRAFT_1_13_2) {
                buf.writeShort(protocolId);
                if (protocolId == -1) {
                    return;
                }
            } else {
                buf.writeBoolean(protocolId != -1);
                if (protocolId == -1) {
                    return;
                }
                ProtocolUtil.writeVarInt(buf, protocolId);
            }
            buf.writeByte(stack.amount());
            if (protocolVersion < MINECRAFT_1_13) {
                buf.writeShort(durability);
            }
            if (stack.nbtData() == null) {
                stack.nbtData(new CompoundTag());
            }
            if (protocolVersion >= MINECRAFT_1_13) {
                stack.nbtData().put("Damage", new IntTag(durability));
            }
            writeDisplayNameTag(stack.displayNameJson(), stack.nbtData(), protocolVersion);
            writeLoreTag(stack.loreJson(), stack.nbtData(), protocolVersion);
            writeHideFlags(stack.hideFlags(), stack.nbtData());
            if (mapping instanceof AbstractLegacyItemNBTProtocolIdMapping) {
                ((AbstractLegacyItemNBTProtocolIdMapping) mapping).apply(stack, protocolVersion);
            }
            buf.markWriterIndex();
            try {
                NamedBinaryTagUtil.writeTag(buf, stack.nbtData(), protocolVersion);
            } catch (Exception e) {
                log.error("Unable to write nbt data for item " + stack, e);
                buf.resetWriterIndex();
                NamedBinaryTagUtil.writeTag(buf, new CompoundTag(), protocolVersion);
            }
        } catch (Exception e) {
            log.error("Unable to write item on protocol version " + protocolVersion, e);
        }
    }

    private static void writeHideFlags(int hideFlags, CompoundTag nbtData) {
        if (nbtData.containsKey("HideFlags") && hideFlags == 0) {
            return; // Don't overwrite existing HideFlags attribute
        }
        nbtData.put("HideFlags", new IntTag(hideFlags));
    }

    private static void writeLoreTag(List<String> loreJson, CompoundTag nbtData, int protocolVersion) {
        if (loreJson == null)
            return;
        CompoundTag display = nbtData.getCompoundTag("display");
        if (display == null) {
            display = new CompoundTag();
        }
        if (protocolVersion < MINECRAFT_1_14) {
            ListTag<StringTag> tag = new ListTag<>(StringTag.class);
            tag.addAll(loreJson.stream().map(s -> new StringTag(CONVERTER.toLegacyText(CONVERTER.fromJson(s)))).collect(Collectors.toList()));
            display.put("Lore", tag);
            nbtData.put("display", display);
        } else {
            ListTag<StringTag> tag = new ListTag<>(StringTag.class);
            tag.addAll(loreJson.stream().map(StringTag::new).collect(Collectors.toList()));
            display.put("Lore", tag);
            nbtData.put("display", display);
        }
    }

    private static void writeDisplayNameTag(String name, CompoundTag nbtData, int protocolVersion) {
        if (name == null)
            return;
        CompoundTag display = nbtData.getCompoundTag("display");
        if (display == null) {
            display = new CompoundTag();
        }
        final StringTag tag = new StringTag(protocolVersion >= MINECRAFT_1_13 ? name : CONVERTER.toLegacyText(CONVERTER.fromJson(name)));
        display.put("Name", tag);
        nbtData.put("display", display);
    }

}
