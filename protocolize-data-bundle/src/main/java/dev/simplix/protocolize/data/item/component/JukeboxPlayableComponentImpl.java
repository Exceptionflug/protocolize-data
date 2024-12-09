package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.api.item.JukeboxSong;
import dev.simplix.protocolize.api.item.component.JukeboxPlayableComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class JukeboxPlayableComponentImpl implements JukeboxPlayableComponent {

    private String identifier;
    private Integer songId;
    private JukeboxSong jukeboxSong;
    private boolean showInTooltip;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(!byteBuf.readBoolean()){
            identifier = ProtocolUtil.readString(byteBuf);
        } else {
            int type = ProtocolUtil.readVarInt(byteBuf);
            if(type != 0){
                songId = type - 1;
            } else {
                songId = null;
                jukeboxSong = new JukeboxSong();
                jukeboxSong.setSoundEvent(StructuredComponentUtil.readSoundEvent(byteBuf, protocolVersion));
                jukeboxSong.setDescription(ChatElement.ofNbt(NamedBinaryTagUtil.readTag(byteBuf, protocolVersion)));
                jukeboxSong.setDuration(byteBuf.readFloat());
                jukeboxSong.setOutputStrength(ProtocolUtil.readVarInt(byteBuf));
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
            if(songId != null){
                ProtocolUtil.writeVarInt(byteBuf, songId + 1);
            } else {
                StructuredComponentUtil.writeSoundEvent(byteBuf, jukeboxSong.getSoundEvent(), protocolVersion);
                NamedBinaryTagUtil.writeTag(byteBuf, jukeboxSong.getDescription().asNbt(), protocolVersion);
                byteBuf.writeFloat(jukeboxSong.getDuration());
                ProtocolUtil.writeVarInt(byteBuf, jukeboxSong.getOutputStrength());
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

        @Override
        public JukeboxPlayableComponent create(String identifier, Integer songId, JukeboxSong jukeboxSong, boolean showInTooltip) {
            return new JukeboxPlayableComponentImpl(identifier, songId, jukeboxSong, showInTooltip);
        }

        @Override
        public String getName() {
            return "minecraft:jukebox_playable";
        }

        @Override
        public JukeboxPlayableComponent createEmpty() {
            return new JukeboxPlayableComponentImpl(null, 0, null, true);
        }

    }

}
