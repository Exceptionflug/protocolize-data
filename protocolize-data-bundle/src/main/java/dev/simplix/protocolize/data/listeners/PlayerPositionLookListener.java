package dev.simplix.protocolize.data.listeners;

import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.listener.AbstractPacketListener;
import dev.simplix.protocolize.api.listener.PacketReceiveEvent;
import dev.simplix.protocolize.api.listener.PacketSendEvent;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.packets.PlayerPosition;
import dev.simplix.protocolize.data.packets.PlayerPositionLook;

/**
 * Date: 29.08.2021
 *
 * @author Exceptionflug
 */
public final class PlayerPositionLookListener extends AbstractPacketListener<PlayerPositionLook> {

    public PlayerPositionLookListener() {
        super(PlayerPositionLook.class, Direction.UPSTREAM, 0);
    }

    @Override
    public void packetReceive(PacketReceiveEvent<PlayerPositionLook> event) {
        ProtocolizePlayer player = event.player();
        if (player != null) {
            player.location().x(event.packet().location().x());
            player.location().y(event.packet().location().y());
            player.location().z(event.packet().location().z());
            player.location().yaw(event.packet().location().yaw());
            player.location().pitch(event.packet().location().pitch());
        }
    }

    @Override
    public void packetSend(PacketSendEvent<PlayerPositionLook> packetSendEvent) {

    }
}
