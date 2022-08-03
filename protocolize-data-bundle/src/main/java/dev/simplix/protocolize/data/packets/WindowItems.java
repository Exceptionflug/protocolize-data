package dev.simplix.protocolize.data.packets;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.item.*;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.mapping.ProtocolIdMapping;
import dev.simplix.protocolize.api.packet.AbstractPacket;
import dev.simplix.protocolize.api.util.ProtocolUtil;
import dev.simplix.protocolize.data.util.LazyBuffer;
import io.netty.buffer.ByteBuf;
import java.util.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * Date: 25.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(fluent = true)
public class WindowItems extends AbstractPacket {

  public static final List<ProtocolIdMapping> MAPPINGS = Arrays.asList(
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_8, MINECRAFT_1_8, 0x30),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_9, MINECRAFT_1_12_2, 0x14),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_13, MINECRAFT_1_13_2, 0x15),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_14, MINECRAFT_1_14_4, 0x14),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_15, MINECRAFT_1_15_2, 0x15),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16, MINECRAFT_1_16_1, 0x14),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_16_2, MINECRAFT_1_16_4, 0x13),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_17, MINECRAFT_1_18_2, 0x14),
          AbstractProtocolMapping.rangedIdMapping(MINECRAFT_1_19, MINECRAFT_LATEST, 0x11)
  );

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private LazyBuffer lazyBuffer = LazyBuffer.empty();

  private short windowId;
  private List<BaseItemStack> items = new ArrayList<>();
  private int count;

  /**
   * @since Protocol 756
   */
  private BaseItemStack cursorItem = ItemStack.NO_DATA;

  /**
   * @since Protocol 756
   */
  private int stateId;

  public WindowItems(short windowId, List<BaseItemStack> items, int stateId) {
    this.windowId = windowId;
    this.items = items;
    this.count = items.size();
    this.stateId = stateId;
  }

  @Override
  public void read(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
    this.windowId = buf.readUnsignedByte();
    if (protocolVersion >= MINECRAFT_1_17_1) {
      this.stateId = ProtocolUtil.readVarInt(buf);
      this.count = ProtocolUtil.readVarInt(buf);
    } else {
      this.count = buf.readShort();
    }

    byte[] data = new byte[buf.readableBytes()];
    buf.readBytes(data);
    this.lazyBuffer = new LazyBuffer(data, (wrappedBuf) -> {
      for (int i = 0; i < this.count; i++) {
        this.items.add(ItemStackSerializer.read(wrappedBuf, protocolVersion));
      }
      if (protocolVersion >= MINECRAFT_1_17_1) {
        this.cursorItem = ItemStackSerializer.read(wrappedBuf, protocolVersion);
      }
    });
  }

  @Override
  public void write(ByteBuf buf, PacketDirection packetDirection, int protocolVersion) {
    buf.writeByte(this.windowId & 0xFF);
    if (protocolVersion >= MINECRAFT_1_17_1) {
      ProtocolUtil.writeVarInt(buf, this.stateId);
      ProtocolUtil.writeVarInt(buf, this.count);
    } else {
      buf.writeShort(this.count);
    }
    this.lazyBuffer.write(buf, () -> {
      for (BaseItemStack item : this.items) {
        if (item==null) {
          item = ItemStack.NO_DATA;
        }
        ItemStackSerializer.write(buf, item, protocolVersion);
      }
      if (protocolVersion >= MINECRAFT_1_17_1) {
        ItemStackSerializer.write(buf, this.cursorItem, protocolVersion);
      }
    });
  }

  public List<BaseItemStack> items() {
    this.lazyBuffer.read();
    return this.items;
  }

  public void items(List<BaseItemStack> items) {
    this.lazyBuffer = LazyBuffer.empty();
    this.items = items;
    this.count = items.size();
  }

  /**
   * @since Protocol 756
   */
  public BaseItemStack cursorItem() {
    this.lazyBuffer.read();
    return this.cursorItem;
  }

}
