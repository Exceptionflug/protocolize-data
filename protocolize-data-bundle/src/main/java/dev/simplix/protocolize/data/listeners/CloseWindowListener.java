package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.api.inventory.InventoryClose;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.data.packets.CloseWindow;

/**
 * Date: 28.08.2021
 *
 * @author Exceptionflug
 */
public final class CloseWindowListener extends AbstractPacketListener<CloseWindow> {

    public CloseWindowListener(Direction direction) {
        super(CloseWindow.class, direction, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<CloseWindow> event) {
        if (event.packet() == null) {
            return;
        }
        Inventory inventory = event.player().registeredInventories().get(event.packet().windowId());
        if (inventory == null) {
            return;
        }
        event.player().registerInventory(event.packet().windowId(), null);
        inventory.closeConsumers().forEach(consumer -> consumer.accept(new InventoryClose(event.player(), inventory,
                null, event.packet().windowId())));
    }

    @Override
    public void packetSend(PacketSendEvent<CloseWindow> packetSendEvent) {
    }

}
