package dev.simplix.protocolize.data.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Date: 26.08.2021
 *
 * @author Exceptionflug
 */
public class LazyBuffer {

    private final byte[] data;
    private final Consumer<ByteBuf> bufConsumer;
    private boolean read;

    public LazyBuffer(byte[] data, Consumer<ByteBuf> consumer) {
        this.data = data;
        this.bufConsumer = consumer;
    }

    public static LazyBuffer empty() {
        LazyBuffer lazyBuffer = new LazyBuffer(new byte[0], byteBuf -> {});
        lazyBuffer.read = true;
        return lazyBuffer;
    }

    public void read() {
        if (read) {
            return;
        }
        bufConsumer.accept(Unpooled.wrappedBuffer(data));
        read = true;
    }

    public void write(ByteBuf buf, Runnable runnable) {
        if (read) {
            runnable.run();
        } else {
            buf.writeBytes(data);
        }
    }

}
