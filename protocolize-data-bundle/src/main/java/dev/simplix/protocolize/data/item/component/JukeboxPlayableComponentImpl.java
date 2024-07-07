package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.item.component.JukeboxPlayableComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import dev.simplix.protocolize.data.util.StructureComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class JukeboxPlayableComponentImpl implements JukeboxPlayableComponent {

    private String identifier;
    private Integer song;
    private SoundEvent soundEvent;
    private ChatElement<?> description;
    private float duration;
    private int outputStrength;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(!byteBuf.readBoolean()){
            identifier = ProtocolUtil.readString(byteBuf);
        } else {
            int type = ProtocolUtil.readVarInt(byteBuf);
            if(type != 0){
                song = type - 1;
            } else {
                song = null;
                soundEvent = StructureComponentUtil.readSoundEvent(byteBuf, protocolVersion);
                description = ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion));
                duration = byteBuf.readFloat();
                outputStrength = ProtocolUtil.readVarInt(byteBuf);
            }
        }
        showInTooltip = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeBoolean(identifier == null);
        if(identifier != null){
            ProtocolUtil.writeString(byteBuf, identifier);
        } else {
            if(song != null){
                ProtocolUtil.writeVarInt(byteBuf, song + 1);
            } else {
                StructureComponentUtil.writeSoundEvent(byteBuf, soundEvent, protocolVersion);
                NamedBinaryTagUtil.writeTag(byteBuf, description.asNbt(), protocolVersion);
                byteBuf.writeFloat(duration);
                ProtocolUtil.writeVarInt(byteBuf, outputStrength);
            }
        }
        byteBuf.writeBoolean(showInTooltip);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<JukeboxPlayableComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 42)
        );

        @Override
        public JukeboxPlayableComponent create(String identifier, Integer song, SoundEvent soundEvent, ChatElement<?> description, float duration, int outputStrength, boolean showInTooltip) {
            return new JukeboxPlayableComponentImpl(identifier, song, soundEvent, description, duration, outputStrength, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:jukebox_playable";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public JukeboxPlayableComponent createEmpty() {
            return new JukeboxPlayableComponentImpl(null, 0, null, null, 0, 0, true);
        }

    }

}
