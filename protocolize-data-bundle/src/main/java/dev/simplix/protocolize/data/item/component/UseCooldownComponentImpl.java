package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.UseCooldownComponent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UseCooldownComponentImpl implements UseCooldownComponent {

    private float cooldown;
    private String cooldownGroup;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        cooldown = byteBuf.readFloat();
        if(byteBuf.readBoolean()) {
            cooldownGroup = ProtocolUtil.readString(byteBuf);
        }
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        byteBuf.writeFloat(cooldown);
        byteBuf.writeBoolean(cooldownGroup != null);
        if(cooldownGroup != null) {
            ProtocolUtil.writeString(byteBuf, cooldownGroup);
        }
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<UseCooldownComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public UseCooldownComponent create(float cooldown, String cooldownGroup) {
            return new UseCooldownComponentImpl(cooldown, cooldownGroup);
        }

        @Override
        public String getName() {
            return "minecraft:use_cooldown";
        }

        @Override
        public UseCooldownComponent createEmpty() {
            return new UseCooldownComponentImpl(0, "");
        }

    }


}
