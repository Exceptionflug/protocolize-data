package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PlayerInteract;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.data.packets.UseItem;
import lombok.extern.slf4j.Slf4j;

/**
 * Date: 31.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class UseItemListener extends AbstractPacketListener<UseItem> {

    public UseItemListener() {
        super(UseItem.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<UseItem> event) {
        if (event.player() == null) {
            return;
        }
        try {
            PlayerInventory playerInventory = event.player().proxyInventory();
            BaseItemStack inHand = playerInventory.item(playerInventory.heldItem() + 36);
            PlayerInteract interact = new PlayerInteract(inHand, null, event.packet().hand(), false);
            event.player().handleInteract(interact);
            if (event.packet().hand() != interact.hand()) {
                event.packet().hand(interact.hand());
                event.markForRewrite();
            }
            if (inHand != null || interact.cancelled()) {
                event.cancelled(true);
            }
        } catch (Throwable throwable) {
            log.debug("Error while handling UseItem packet", throwable);
        }
    }

    @Override
    public void packetSend(PacketSendEvent<UseItem> packetSendEvent) {

    }
}
