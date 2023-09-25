package dev.simplix.protocolize.data.util;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.tag.Tag;

import java.io.IOException;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_20_2;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public final class NamedBinaryTagUtil {

    private NamedBinaryTagUtil() {
    }

    public static Tag<?> readTag(ByteBuf byteBuf, int protocolVersion) throws IOException {
        Preconditions.checkNotNull(byteBuf, "ByteBuf cannot be null");
        int i = byteBuf.readerIndex();
        short b0 = byteBuf.readUnsignedByte();
        if (b0 == 0) {
            return null;
        }
        byteBuf.readerIndex(i);
        try (NBTInputStream inputStream = new NBTInputStream(new ByteBufInputStream(byteBuf))) {
            if (protocolVersion >= MINECRAFT_1_20_2) {
                return inputStream.readRawTag(32);
            } else {
                return inputStream.readTag(32).getTag();
            }
        }
    }

    public static void writeTag(ByteBuf buf, Tag<?> tag, int protocolVersion) throws IOException {
        Preconditions.checkNotNull(buf, "ByteBuf cannot be null");
        Preconditions.checkNotNull(tag, "Tag cannot be null");
        try (NBTOutputStream outputStream = new NBTOutputStream(new ByteBufOutputStream(buf))) {
            if (protocolVersion >= MINECRAFT_1_20_2) {
                outputStream.writeByte(tag.getID());
                outputStream.writeRawTag(tag, 32);
            } else {
                outputStream.writeTag(tag, 32);
            }
        }
    }

}
