package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.NoteBlockSoundComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_6;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_21;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
public class NoteBlockSoundComponentImpl implements NoteBlockSoundComponent {

    private String sound;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        sound = ProtocolUtil.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeString(byteBuf, sound);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<NoteBlockSoundComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_21, MINECRAFT_LATEST, 48),
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_1_20_6, 47)
        );

        @Override
        public NoteBlockSoundComponent create(String sound) {
            return new NoteBlockSoundComponentImpl(sound);
        }

        @Override
        public String getName() {
            return "minecraft:note_block_sound";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public NoteBlockSoundComponent createEmpty() {
            return new NoteBlockSoundComponentImpl("");
        }

    }

}
