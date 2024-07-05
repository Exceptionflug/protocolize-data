package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.BannerLayer;
import dev.simplix.protocolize.api.item.DyeColor;
import dev.simplix.protocolize.api.item.component.BannerPatternsComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class BannerPatternsComponentImpl implements BannerPatternsComponent {

    private List<BannerLayer> layers;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int count = ProtocolUtil.readVarInt(byteBuf);
        for (int i = 0; i < count; i++) {
            BannerLayer layer = new BannerLayer();
            int layerType = ProtocolUtil.readVarInt(byteBuf);
            if(layerType > 0) {
                layer.setPatternType(layerType - 1);
            } else {
                layer.setDirect(true);
                layer.setIdentifier(ProtocolUtil.readString(byteBuf));
                layer.setTranslationKey(ProtocolUtil.readString(byteBuf));
            }
            layer.setColor(DyeColor.values()[ProtocolUtil.readVarInt(byteBuf)]);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, layers.size());
        for (BannerLayer layer : layers) {
            if(!layer.isDirect()) {
                ProtocolUtil.writeVarInt(byteBuf, layer.getPatternType() + 1);
            } else {
                ProtocolUtil.writeString(byteBuf, layer.getIdentifier());
                ProtocolUtil.writeString(byteBuf, layer.getTranslationKey());
            }
            ProtocolUtil.writeVarInt(byteBuf, layer.getColor().ordinal());
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public void addLayer(BannerLayer layer) {
        layers.add(layer);
    }

    @Override
    public void removeLayer(BannerLayer layer) {
        layers.remove(layer);
    }

    @Override
    public void removeAllLayers() {
        layers.clear();
    }

    public static class Type implements StructuredComponentType<BannerPatternsComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 49),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 48)
        );

        @Override
        public BannerPatternsComponent create(List<BannerLayer> layers) {
            return new BannerPatternsComponentImpl(layers);
        }

        @Override
        public String getName() {
            return "minecraft:banner_patterns";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public BannerPatternsComponent createEmpty() {
            return new BannerPatternsComponentImpl(new ArrayList<>(0));
        }

    }

}
