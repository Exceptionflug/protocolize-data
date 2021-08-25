package dev.simplix.protocolize.data.util;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.Tag;

import java.io.IOException;

/**
 * Date: 24.08.2021
 *
 * @author Exceptionflug
 */
public final class NamedBinaryTagUtil {

    private NamedBinaryTagUtil() {}

    public static NamedTag readTag(ByteBuf byteBuf) throws IOException {
        Preconditions.checkNotNull(byteBuf, "ByteBuf cannot be null");
        int i = byteBuf.readerIndex();
        short b0 = byteBuf.readUnsignedByte();
        if (b0 == 0) {
            return null;
        }
        byteBuf.readerIndex(i);
        try (NBTInputStream inputStream = new NBTInputStream(new ByteBufInputStream(byteBuf))) {
            return inputStream.readTag(32);
        }
    }

    public static void writeTag(ByteBuf buf, Tag<?> tag) throws IOException {
        Preconditions.checkNotNull(buf, "ByteBuf cannot be null");
        Preconditions.checkNotNull(tag, "Tag cannot be null");
        try (NBTOutputStream outputStream = new NBTOutputStream(new ByteBufOutputStream(buf))) {
            outputStream.writeTag(tag, 32);
        }
    }

}
