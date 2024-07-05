package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.Trim;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.TrimComponent;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class TrimComponentImpl implements TrimComponent {

    private Trim trim;
    boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int trimType = ProtocolUtil.readVarInt(byteBuf);
        if(trimType != 0){
            trim.setMaterialType(trimType - 1);
        } else {
            Trim.TrimMaterial material = new Trim.TrimMaterial();
            material.setAssetName(ProtocolUtil.readString(byteBuf));
            material.setIngredient(ProtocolUtil.readVarInt(byteBuf));
            material.setItemModelIndex(byteBuf.readFloat());
            int count = ProtocolUtil.readVarInt(byteBuf);
            Map<Integer, String> overrides = new HashMap<>(count);
            for(int i = 0; i < count; i++){
                overrides.put(ProtocolUtil.readVarInt(byteBuf), ProtocolUtil.readString(byteBuf));
            }
            material.setOverrides(overrides);
            material.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
        }
        int patternType = ProtocolUtil.readVarInt(byteBuf);
        if(patternType != 0){
            trim.setPatternType(patternType - 1);
        } else {
            Trim.TrimPattern pattern = new Trim.TrimPattern();
            pattern.setAssetName(ProtocolUtil.readString(byteBuf));
            pattern.setTemplateItem(ProtocolUtil.readVarInt(byteBuf));
            pattern.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
            pattern.setDecal(byteBuf.readBoolean());
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(trim.getMaterialType() != null) {
            ProtocolUtil.writeVarInt(byteBuf, trim.getMaterialType() + 1);
        } else {
            Trim.TrimMaterial material = trim.getTrimMaterial();
            ProtocolUtil.writeString(byteBuf, material.getAssetName());
            ProtocolUtil.writeVarInt(byteBuf, material.getIngredient());
            byteBuf.writeFloat(material.getItemModelIndex());
            ProtocolUtil.writeVarInt(byteBuf, material.getOverrides().size());
            for(Map.Entry<Integer, String> entry : material.getOverrides().entrySet()){
                ProtocolUtil.writeVarInt(byteBuf, entry.getKey());
                ProtocolUtil.writeString(byteBuf, entry.getValue());
            }
            NamedBinaryTagUtil.writeTag(byteBuf, material.getDescription().asNbt(), protocolVersion);
        }
        if(trim.getPatternType() != null) {
            ProtocolUtil.writeVarInt(byteBuf, trim.getPatternType() + 1);
        } else {
            Trim.TrimPattern pattern = trim.getTrimPattern();
            ProtocolUtil.writeString(byteBuf, pattern.getAssetName());
            ProtocolUtil.writeVarInt(byteBuf, pattern.getTemplateItem());
            NamedBinaryTagUtil.writeTag(byteBuf, pattern.getDescription().asNbt(), protocolVersion);
            byteBuf.writeBoolean(pattern.isDecal());
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<TrimComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 35)
        );

        @Override
        public TrimComponent create(Trim trim, boolean showInTooltip) {
            return new TrimComponentImpl(trim, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:trim";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public TrimComponent createEmpty() {
            return new TrimComponentImpl(new Trim(null, null, null, null), true);
        }

    }

}
