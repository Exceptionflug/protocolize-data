package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.CreativeSlotLockComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import io.netty.buffer.ByteBuf;

public class CreativeSlotLockComponentImpl implements CreativeSlotLockComponent {

    @Override
    public void read(ByteBuf byteBuf, int i) throws Exception {
    }

    @Override
    public void write(ByteBuf byteBuf, int i) throws Exception {
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<CreativeSlotLockComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public CreativeSlotLockComponent create() {
            return new CreativeSlotLockComponentImpl();
        }

        @Override
        public String getName() {
            return "minecraft:creative_slot_lock";
        }

        @Override
        public CreativeSlotLockComponent createEmpty() {
            return create();
        }

    }

}
