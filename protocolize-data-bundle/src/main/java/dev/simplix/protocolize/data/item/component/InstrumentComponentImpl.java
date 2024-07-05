package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.item.component.InstrumentComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.mapping.ProtocolMapping;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.Instrument;
import dev.simplix.protocolize.data.util.StructureComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_5;
import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_LATEST;

@Data
@AllArgsConstructor
@Slf4j(topic = "Protocolize")
public class InstrumentComponentImpl implements InstrumentComponent {

    private Instrument instrument;
    private SoundEvent soundEvent;
    private float duration, range;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        int type = ProtocolUtil.readVarInt(byteBuf);
        if(type != 0){
            instrument = MAPPING_PROVIDER.mapIdToEnum(type - 1, protocolVersion, Instrument.class);
        } else {
            soundEvent = StructureComponentUtil.readSoundEvent(byteBuf, protocolVersion);
            duration = byteBuf.readFloat();
            range = byteBuf.readFloat();
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(instrument != null){
            ProtocolMapping mapping = MAPPING_PROVIDER.mapping(instrument, protocolVersion);
            if (!(mapping instanceof ProtocolIdMapping)) {
                log.warn("{} cannot be used on protocol version {}", instrument.name(), protocolVersion);
                ProtocolUtil.writeVarInt(byteBuf, 1);
            } else {
                ProtocolUtil.writeVarInt(byteBuf, ((ProtocolIdMapping) mapping).id() + 1);
            }
        } else {
            StructureComponentUtil.writeSoundEvent(byteBuf, soundEvent, protocolVersion);
            byteBuf.writeFloat(duration);
            byteBuf.writeFloat(range);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<InstrumentComponent>, Factory {

        public static Type INSTANCE = new Type();

        private static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
            AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_20_5, MINECRAFT_LATEST, 40)
        );

        @Override
        public InstrumentComponent create(Instrument instrument) {
            return new InstrumentComponentImpl(instrument, null, 0, 0);
        }

        @Override
        public InstrumentComponent create(SoundEvent soundEvent, float duration, float range) {
            return new InstrumentComponentImpl(null, soundEvent, duration, range);
        }

        @Override
        public String getName() {
            return "minecraft:instrument";
        }

        @Override
        public List<ProtocolIdMapping> getMappings() {
            return MAPPINGS;
        }

        @Override
        public InstrumentComponent createEmpty() {
            return new InstrumentComponentImpl(Instrument.values()[0], null, 0, 0);
        }

    }

}
