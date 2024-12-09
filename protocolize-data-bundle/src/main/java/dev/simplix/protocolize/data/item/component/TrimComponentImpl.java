package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.TrimMaterial;
import dev.simplix.protocolize.api.item.TrimPattern;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.TrimComponent;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21_2;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class TrimComponentImpl implements TrimComponent {

    private ItemType materialItem;
    private TrimMaterial trimMaterial;
    private ItemType patternItem;
    private TrimPattern trimPattern;
    private boolean showInTooltip;

    private static MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int trimType = ProtocolUtil.readVarInt(byteBuf);
        if(trimType != 0){
            materialItem = MAPPING_PROVIDER.mapIdToEnum(trimType - 1, protocolVersion, ItemType.class);
        } else {
            TrimMaterial material = new TrimMaterial();
            material.setAssetName(ProtocolUtil.readString(byteBuf));
            material.setIngredient(MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, ItemType.class));
            material.setItemModelIndex(byteBuf.readFloat());
            int count = ProtocolUtil.readVarInt(byteBuf);
            Map<Object, String> overrides = new HashMap<>(count);
            for(int i = 0; i < count; i++){
                overrides.put((protocolVersion >= MINECRAFT_1_21_2) ? ProtocolUtil.readString(byteBuf) : ProtocolUtil.readVarInt(byteBuf), ProtocolUtil.readString(byteBuf));
            }
            material.setOverrides(overrides);
            material.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
        }
        int patternType = ProtocolUtil.readVarInt(byteBuf);
        if(patternType != 0){
            patternItem = MAPPING_PROVIDER.mapIdToEnum(patternType - 1, protocolVersion, ItemType.class);
        } else {
            TrimPattern pattern = new TrimPattern();
            pattern.setAssetName(ProtocolUtil.readString(byteBuf));
            pattern.setTemplateItem(MAPPING_PROVIDER.mapIdToEnum(ProtocolUtil.readVarInt(byteBuf), protocolVersion, ItemType.class));
            pattern.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
            pattern.setDecal(byteBuf.readBoolean());
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(materialItem != null) {
            if(!StructuredComponentUtil.writeItemMapping(byteBuf, materialItem, protocolVersion, 1))
                return;
        } else {
            ProtocolUtil.writeString(byteBuf, trimMaterial.getAssetName());
            StructuredComponentUtil.writeItemMapping(byteBuf, trimMaterial.getIngredient(), protocolVersion);
            byteBuf.writeFloat(trimMaterial.getItemModelIndex());
            ProtocolUtil.writeVarInt(byteBuf, trimMaterial.getOverrides().size());
            for(Map.Entry<Object, String> entry : trimMaterial.getOverrides().entrySet()){
                if(protocolVersion >= MINECRAFT_1_21_2){
                    ProtocolUtil.writeString(byteBuf, (String) entry.getKey());
                } else {
                    ProtocolUtil.writeVarInt(byteBuf, (int) entry.getKey());
                }
                ProtocolUtil.writeString(byteBuf, entry.getValue());
            }
            NamedBinaryTagUtil.writeTag(byteBuf, trimMaterial.getDescription().asNbt(), protocolVersion);
        }
        if(patternItem != null) {
            if(!StructuredComponentUtil.writeItemMapping(byteBuf, patternItem, protocolVersion, 1))
                return;
        } else {
            ProtocolUtil.writeString(byteBuf, trimPattern.getAssetName());
            StructuredComponentUtil.writeItemMapping(byteBuf, trimPattern.getTemplateItem(), protocolVersion);
            NamedBinaryTagUtil.writeTag(byteBuf, trimPattern.getDescription().asNbt(), protocolVersion);
            byteBuf.writeBoolean(trimPattern.isDecal());
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<TrimComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public TrimComponent create(ItemType materialItem, TrimMaterial trimMaterial, ItemType patternItem, TrimPattern trimPattern, boolean showInTooltip) {
            return new TrimComponentImpl(materialItem, trimMaterial, patternItem, trimPattern, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:trim";
        }

        @Override
        public TrimComponent createEmpty() {
            return new TrimComponentImpl(ItemType.AIR, null, ItemType.AIR, null, true);
        }

    }

}
