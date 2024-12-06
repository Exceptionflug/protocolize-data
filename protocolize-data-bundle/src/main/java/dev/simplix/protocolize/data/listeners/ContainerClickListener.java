package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClick;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.inventory.InventoryType;
import dev.simplix.protocolize.data.packets.ClickWindow;
import dev.simplix.protocolize.data.packets.ConfirmTransaction;
import dev.simplix.protocolize.data.packets.SetSlot;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_17;

/**
 * Date: 28.08.2021
 *
 * @author Exceptionflug
 */
public final class ContainerClickListener extends AbstractPacketListener<ClickWindow> {

    public ContainerClickListener() {
        super(ClickWindow.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<ClickWindow> event) {
        ClickWindow clickWindow = event.packet();
        if (event.player() == null) {
            return;
        }
        Inventory inventory = event.player().registeredInventories().get(clickWindow.windowId());
        if (inventory == null) {
            return;
        }
        short slot = clickWindow.slot();
        if (inventory.type() == InventoryType.BREWING_STAND && event.player().protocolVersion() == ProtocolVersions.MINECRAFT_1_8) {
            if (slot >= 4) {
                slot++;
            }
        }
        InventoryClick click = new InventoryClick(event.player(), inventory, clickWindow.clickType(),
            clickWindow.slot(), clickWindow.windowId(), clickWindow.actionNumber(), false);
        inventory.clickConsumers().forEach(consumer -> consumer.accept(click));
        if (clickWindow.actionNumber() != click.actionNumber()) {
            clickWindow.actionNumber(click.actionNumber());
            event.markForRewrite();
        }
        if (clickWindow.windowId() != click.windowId()) {
            clickWindow.windowId(click.windowId());
            event.markForRewrite();
        }
        if (clickWindow.clickType() != click.clickType() && click.clickType() != null) {
            clickWindow.clickType(click.clickType());
            event.markForRewrite();
        }
        if (slot != click.slot()) {
            clickWindow.slot((short) click.slot());
            event.markForRewrite();
        }
        ClickType clickType = click.clickType();
        event.cancelled(true);
        if (event.player().registeredInventories().get(clickWindow.windowId()) == null) {
            return; // Inv maybe closed during click handle
        }
        if (inventory.type() != InventoryType.PLAYER) {
            event.player().openInventory(inventory);
        } else {
            event.player().proxyInventory().update();
        }
        if (clickType.name().startsWith("NUMBER_BUTTON")) {
            if (event.player().protocolVersion() < MINECRAFT_1_17) {
                event.player().sendPacket(new ConfirmTransaction((byte) clickWindow.windowId(), (short) clickWindow.actionNumber(), false));
            }
            event.player().sendPacket(new SetSlot((byte) 0, (short) (clickType.button() + 36), ItemStack.NO_DATA, 0));
        } else if (clickType.name().startsWith("SHIFT_")) {
            if (event.player().protocolVersion() < MINECRAFT_1_17) {
                event.player().sendPacket(new ConfirmTransaction((byte) clickWindow.windowId(), (short) clickWindow.actionNumber(), false));
            }
            event.player().sendPacket(new SetSlot((byte) 0, (short) 44, ItemStack.NO_DATA, 0));
        } else {
            event.player().sendPacket(new SetSlot((byte) -1, (short) -1, ItemStack.NO_DATA, 0));
        }
    }

    @Override
    public void packetSend(PacketSendEvent<ClickWindow> packetSendEvent) {

    }

}
