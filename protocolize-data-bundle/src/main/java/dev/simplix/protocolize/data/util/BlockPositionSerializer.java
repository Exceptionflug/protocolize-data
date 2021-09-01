package dev.simplix.protocolize.data.util;

import dev.simplix.protocolize.api.BlockPosition;
import io.netty.buffer.ByteBuf;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_14;

/**
 * Date: 25.08.2021
 *
 * @author Exceptionflug
 */
public final class BlockPositionSerializer {

    private BlockPositionSerializer() {
    }

    public static BlockPosition read(ByteBuf byteBuf, int protocolVersion) {
        long val = byteBuf.readLong();
        int x = (int) (val >> 38);
        int y;
        int z;
        if (protocolVersion < MINECRAFT_1_14) {
            y = (int) ((val >> 26) & 0xFFF);
            z = (int) (val << 38 >> 38);
        } else {
            y = (int) val & 0xFFF;
            z = (int) (val << 38 >> 38) >> 12;
        }
        return new BlockPosition(x, y, z);
    }

    public static void write(ByteBuf byteBuf, BlockPosition position, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_14) {
            byteBuf.writeLong(((long) position.x() & 67108863) << 38 | ((long) position.y() & 4095) << 26 | ((long) position.z() & 67108863) << 0);
        } else {
            byteBuf.writeLong((((long) position.x() & 0x3FFFFFF) << 38) | (((long) position.z() & 0x3FFFFFF) << 12) | ((long) position.y() & 0xFFF));
        }
    }

}
