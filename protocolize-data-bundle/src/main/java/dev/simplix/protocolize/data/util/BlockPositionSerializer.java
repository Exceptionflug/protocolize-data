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

    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final int PACKED_X_LENGTH = 1 + log2(smallestEncompassingPowerOfTwo(30000000));
    private static final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
    private static final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
    private static final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
    private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
    private static final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
    private static final int Y_OFFSET = 0;
    private static final int Z_OFFSET = PACKED_Y_LENGTH;
    private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;

    private BlockPositionSerializer() {
    }

    public static BlockPosition read(ByteBuf byteBuf, int protocolVersion) {
        long val = byteBuf.readLong();
        int x;
        int y;
        int z;
        if (protocolVersion < MINECRAFT_1_14) {
            x = (int) (val >> 38);
            y = (int) ((val >> 26) & 0xFFF);
            z = (int) (val << 38 >> 38);
        } else {
            x = getX(val);
            y = getY(val);
            z = getZ(val);
        }
        return new BlockPosition(x, y, z);
    }

    public static void write(ByteBuf byteBuf, BlockPosition position, int protocolVersion) {
        if (protocolVersion < MINECRAFT_1_14) {
            byteBuf.writeLong(((long) position.x() & 67108863) << 38 | ((long) position.y() & 4095) << 26 | ((long) position.z() & 67108863) << 0);
        } else {
            byteBuf.writeLong(asLong(position.x(), position.y(), position.z()));
        }
    }

    private static long asLong(int x, int y, int z) {
        long i = 0L;
        i = i | ((long)x & PACKED_X_MASK) << X_OFFSET;
        i = i | ((long)y & PACKED_Y_MASK) << 0;
        return i | ((long)z & PACKED_Z_MASK) << Z_OFFSET;
    }

    private static int getZ(long val) {
        return (int)(val << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
    }

    public static int getY(long val) {
        return (int)(val << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    private static int getX(long val) {
        return (int)(val << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
    }

    private static int smallestEncompassingPowerOfTwo(int val) {
        int i = val - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

    private static boolean isPowerOfTwo(int val) {
        return val != 0 && (val & val - 1) == 0;
    }

    private static int log2(int val) {
        return ceillog2(val) - (isPowerOfTwo(val) ? 0 : 1);
    }

    private static int ceillog2(int val) {
        val = isPowerOfTwo(val) ? val : smallestEncompassingPowerOfTwo(val);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)val * 125613361L >> 27) & 31];
    }

}
