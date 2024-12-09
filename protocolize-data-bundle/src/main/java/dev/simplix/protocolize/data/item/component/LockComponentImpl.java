package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.LockComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.data.util.NamedBinaryTagUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

@Data
@AllArgsConstructor
public class LockComponentImpl implements LockComponent {

    /* This contains ItemPredicates and more structured components but not as stream codecs... Can be looked at, at a later date */
    private Tag<?> lock;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(protocolVersion <= MINECRAFT_1_21_1) {
            lock = NamedBinaryTagUtil.readTag(byteBuf, protocolVersion); // StringTag
        } else {
            CompoundTag compoundTag = (CompoundTag) NamedBinaryTagUtil.readTag(byteBuf, protocolVersion); // CompoundTag
            if(compoundTag != null && compoundTag.containsKey("lock")){
                lock = compoundTag.get("lock");
            }
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        if(protocolVersion <= MINECRAFT_1_21_1) {
            NamedBinaryTagUtil.writeTag(byteBuf, lock, protocolVersion); // StringTag
        } else {
            CompoundTag compoundTag = new CompoundTag();
            if(lock != null){
                compoundTag.put("lock", lock);
            }
            NamedBinaryTagUtil.writeTag(byteBuf, compoundTag, protocolVersion); // CompoundTag
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<LockComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public LockComponent create(Tag<?> lock) {
            return new LockComponentImpl(lock);
        }

        @Override
        public String getName() {
            return "minecraft:lock";
        }

        @Override
        public LockComponent createEmpty() {
            return new LockComponentImpl(null);
        }

    }

}
