package dev.simplix.protocolize.data.item.component;

import dev.simplix.protocolize.api.item.component.StructuredComponentType;
import dev.simplix.protocolize.api.item.component.TooltipStyleComponent;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TooltipStyleComponentImpl implements TooltipStyleComponent {

    private String style;

    @Override
    public void read(ByteBuf byteBuf, int protocolVersion) throws Exception {
        style = ProtocolUtil.readString(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf, int protocolVersion) throws Exception {
        ProtocolUtil.writeString(byteBuf, style);
    }

    @Override
    public StructuredComponentType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements StructuredComponentType<TooltipStyleComponent>, Factory {

        public static Type INSTANCE = new Type();

        @Override
        public TooltipStyleComponent create(String style) {
            return new TooltipStyleComponentImpl(style);
        }

        @Override
        public String getName() {
            return "minecraft:tooltip_style";
        }

        @Override
        public TooltipStyleComponent createEmpty() {
            return new TooltipStyleComponentImpl("");
        }

    }

}
