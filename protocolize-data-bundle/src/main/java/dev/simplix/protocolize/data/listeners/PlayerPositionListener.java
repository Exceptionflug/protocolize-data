package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.packets.PlayerPosition;

/**
 * Date: 29.08.2021
 *
 * @author Exceptionflug
 */
public final class PlayerPositionListener extends AbstractPacketListener<PlayerPosition> {

    public PlayerPositionListener() {
        super(PlayerPosition.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<PlayerPosition> event) {
        ProtocolizePlayer player = event.player();
        if (player != null) {
            player.location().x(event.packet().location().x());
            player.location().y(event.packet().location().y());
            player.location().z(event.packet().location().z());
        }
    }

    @Override
    public void packetSend(PacketSendEvent<PlayerPosition> packetSendEvent) {

    }
}
