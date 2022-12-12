package dev.simplix.protocolize.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.listeners.*;
import dev.simplix.protocolize.data.packets.*;
import dev.simplix.protocolize.data.registries.Registries;
import dev.simplix.protocolize.data.registries.RegistryEntry;
import dev.simplix.protocolize.data.registries.SoundRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.MINECRAFT_1_19_3;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
public class DataModule implements ProtocolizeModule {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public void registerMappings(MappingProvider mappingProvider) {
        for (int i = ProtocolVersions.MINECRAFT_1_13; i <= ProtocolVersions.MINECRAFT_LATEST; i++) {
            registerMappingsForProtocol(mappingProvider, i);
        }
    }

    private void registerMappingsForProtocol(MappingProvider provider, int i) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + i + "/registries.json")) {
            if (stream == null) {
                registerItemsUsingLegacyFormat(provider, i);
                return;
            }
            Registries registries = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Registries.class);
            registerItemMappings(registries.itemRegistry().entries(), provider, i);
            registerSoundMappings(registries.soundRegistry(), provider, i);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version " + i, e);
        }
    }

    private void registerItemsUsingLegacyFormat(MappingProvider provider, int i) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + i + "/items.json")) {
            if (stream == null) {
                return;
            }
            Map<String, RegistryEntry> legacy = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), new TypeToken<Map<String, RegistryEntry>>() {
            }.getType());
            registerItemMappings(legacy, provider, i);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version " + i, e);
        }
    }

    private void registerItemMappings(Map<String, RegistryEntry> entries, MappingProvider provider, int i) {
        for (String type : entries.keySet()) {
            String name = type.substring("minecraft:".length()).toUpperCase(Locale.ROOT);
            try {
                ItemType itemType = ItemType.valueOf(name);
                int id = entries.get(type).protocolId();
                provider.registerMapping(itemType, AbstractProtocolMapping.rangedIdMapping(i, i, id));
            } catch (IllegalArgumentException e) {
                log.warn("Don't know what item " + name + " was at protocol " + i);
            }
        }
    }

    private void registerSoundMappings(SoundRegistry registry, MappingProvider provider, int i) {
        if (i >= MINECRAFT_1_19_3) {
            for (String type : registry.entries().keySet()) {
                String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT);
                try {
                    Sound sound = Sound.valueOf(name);
                    RegistryEntry entry = registry.entries().get(type);
                    provider.registerMapping(sound, AbstractProtocolMapping.rangedIdMapping(i, i, entry.protocolId()));
                } catch (IllegalArgumentException e) {
                    log.warn("Don't know what sound " + name + " was at protocol " + i);
                }
            }

        } else {
            for (String type : registry.entries().keySet()) {
                String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT);
                try {
                    Sound sound = Sound.valueOf(name);
                    provider.registerMapping(sound, AbstractProtocolMapping.rangedStringMapping(i, i, type));
                } catch (IllegalArgumentException e) {
                    log.warn("Don't know what sound " + name + " was at protocol " + i);
                }
            }
        }
    }

    @Override
    public void registerPackets(ProtocolRegistrationProvider registrationProvider) {
        if (registrationProvider == null) {
            return;
        }
        // CLIENTBOUND
        registrationProvider.registerPacket(WindowItems.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, WindowItems.class);
        registrationProvider.registerPacket(SetSlot.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, SetSlot.class);
        registrationProvider.registerPacket(HeldItemChange.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, HeldItemChange.class);
        registrationProvider.registerPacket(WindowProperty.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, WindowProperty.class);
        registrationProvider.registerPacket(OpenWindow.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, OpenWindow.class);
        registrationProvider.registerPacket(ConfirmTransaction.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, ConfirmTransaction.class);
        registrationProvider.registerPacket(CloseWindow.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, CloseWindow.class);
        registrationProvider.registerPacket(NamedSoundEffect.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, NamedSoundEffect.class);

        // SERVERBOUND
        registrationProvider.registerPacket(UseItem.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, UseItem.class);
        registrationProvider.registerPacket(HeldItemChange.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, HeldItemChange.class);
        registrationProvider.registerPacket(BlockPlacement.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, BlockPlacement.class);
        registrationProvider.registerPacket(ConfirmTransaction.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, ConfirmTransaction.class);
        registrationProvider.registerPacket(ClickWindow.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, ClickWindow.class);
        registrationProvider.registerPacket(CloseWindow.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, CloseWindow.class);
        registrationProvider.registerPacket(PlayerPosition.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerPosition.class);
        registrationProvider.registerPacket(PlayerPositionLook.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerPositionLook.class);
        registrationProvider.registerPacket(PlayerLook.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerLook.class);

        PacketListenerProvider listenerProvider = Protocolize.listenerProvider();
        listenerProvider.registerListener(new CloseWindowListener(Direction.UPSTREAM));
        listenerProvider.registerListener(new CloseWindowListener(Direction.DOWNSTREAM));
        listenerProvider.registerListener(new ClickWindowListener());
        listenerProvider.registerListener(new PlayerPositionListener());
        listenerProvider.registerListener(new PlayerPositionLookListener());
        listenerProvider.registerListener(new PlayerLookListener());
        listenerProvider.registerListener(new UseItemListener());
        listenerProvider.registerListener(new BlockPlacementListener());
    }

}
