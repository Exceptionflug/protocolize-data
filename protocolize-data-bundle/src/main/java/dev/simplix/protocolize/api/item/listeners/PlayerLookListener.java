package dev.simplix.protocolize.api.item.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.packets.PlayerLook;
import dev.simplix.protocolize.data.packets.PlayerPositionLook;

/**
 * Date: 29.08.2021
 *
 * @author Exceptionflug
 */
public final class PlayerLookListener extends AbstractPacketListener<PlayerLook> {

    public PlayerLookListener() {
        super(PlayerLook.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<PlayerLook> event) {
        ProtocolizePlayer player = event.player();
        if (player != null) {
            player.location().yaw(event.packet().yaw());
            player.location().pitch(event.packet().pitch());
        }
    }

    @Override
    public void packetSend(PacketSendEvent<PlayerLook> packetSendEvent) {

    }
}
