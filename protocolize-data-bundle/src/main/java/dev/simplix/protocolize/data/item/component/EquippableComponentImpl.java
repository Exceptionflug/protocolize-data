package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.EquipmentSlot;
import dev.simplix.protocolize.api.item.SoundEvent;
import dev.simplix.protocolize.api.item.component.EquippableComponent;
import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.util.Either;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.EntityType;
import dev.simplix.protocolize.data.util.StructuredComponentUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EquippableComponentImpl implements EquippableComponent {

    private EquipmentSlot slot;
    private SoundEvent equipSound;
    private String model;
    private String cameraOverlay;
    private Either<String, List<EntityType>> allowedEntities;
    private boolean dispensable;
    private boolean swappable;
    private boolean damageOnHurt;

    private static final MappingProvider MAPPING_PROVIDER = Protocolize.mappingProvider();

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        slot = EquipmentSlot.values()[ProtocolUtil.readVarInt(byteBuf)];
        equipSound = StructuredComponentUtil.readSoundEvent(byteBuf, protocolVersion);
        if(byteBuf.readBoolean()) {
            model = ProtocolUtil.readString(byteBuf);
        }
        if(byteBuf.readBoolean()) {
            cameraOverlay = ProtocolUtil.readString(byteBuf);
        }
        if(byteBuf.readBoolean()) {
            allowedEntities = StructuredComponentUtil.readHolderSet(byteBuf, EntityType.class, protocolVersion);
        }
        dispensable = byteBuf.readBoolean();
        swappable = byteBuf.readBoolean();
        damageOnHurt = byteBuf.readBoolean();
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeVarInt(byteBuf, slot.ordinal());
        StructuredComponentUtil.writeSoundEvent(byteBuf, equipSound, protocolVersion);
        byteBuf.writeBoolean(model != null);
        if(model != null) {
            ProtocolUtil.writeString(byteBuf, model);
        }
        byteBuf.writeBoolean(cameraOverlay != null);
        if(cameraOverlay != null) {
            ProtocolUtil.writeString(byteBuf, cameraOverlay);
        }
        byteBuf.writeBoolean(allowedEntities != null);
        if(allowedEntities != null) {
            StructuredComponentUtil.writeHolderSet(byteBuf, allowedEntities, EntityType.class, protocolVersion);
        }
        byteBuf.writeBoolean(dispensable);
        byteBuf.writeBoolean(swappable);
        byteBuf.writeBoolean(damageOnHurt);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<EquippableComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public EquippableComponent create(EquipmentSlot equipmentSlot, SoundEvent equipSound, String model, String cameraOverlay, Either<String, List<EntityType>> allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt) {
            return new EquippableComponentImpl(equipmentSlot, equipSound, model, cameraOverlay, allowedEntities, dispensable, swappable, damageOnHurt);
        }

        @Override
        public String getName() {
            return "minecraft:equippable";
        }

        @Override
        public EquippableComponent createEmpty() {
            return create(EquipmentSlot.MAINHAND, null, null, null, null, false, false, false);
        }

    }

}
