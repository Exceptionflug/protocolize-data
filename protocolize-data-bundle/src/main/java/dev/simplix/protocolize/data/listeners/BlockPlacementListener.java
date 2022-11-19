package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PlayerInteract;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.api.item.BaseItemStack;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.data.packets.BlockPlacement;
import lombok.extern.slf4j.Slf4j;

/**
 * Date: 31.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j
public final class BlockPlacementListener extends AbstractPacketListener<BlockPlacement> {

    public BlockPlacementListener() {
        super(BlockPlacement.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<BlockPlacement> event) {
        if (event.player() == null) {
            return;
        }
        try {
            PlayerInventory playerInventory = event.player().proxyInventory();
            BaseItemStack inHand = playerInventory.item(playerInventory.heldItem() + 36);
            PlayerInteract interact = new PlayerInteract(inHand, event.packet().position(), event.packet().hand(), false);
            event.player().handleInteract(interact);
            if (!event.packet().position().equals(interact.clickedBlockPosition())) {
                event.packet().position(interact.clickedBlockPosition());
                event.markForRewrite();
            }
            if (event.packet().hand() != interact.hand()) {
                event.packet().hand(interact.hand());
                event.markForRewrite();
            }
            if (inHand != null || interact.cancelled()) {
                event.cancelled(true);
            }
        } catch (Throwable throwable) {
            log.debug("Error while handling BlockPlacement packet", throwable);
        }
    }

    @Override
    public void packetSend(PacketSendEvent<BlockPlacement> packetSendEvent) {

    }

}
