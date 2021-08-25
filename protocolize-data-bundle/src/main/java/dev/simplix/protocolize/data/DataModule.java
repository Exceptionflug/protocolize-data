package dev.simplix.protocolize.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.registries.Registries;
import dev.simplix.protocolize.data.registries.RegistryEntry;
import dev.simplix.protocolize.data.registries.SoundRegistry;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

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
            Registries registries = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Registries.class);
            registerItemMappings(registries.itemRegistry().entries(), provider, i);
            registerSoundMappings(registries.soundRegistry(), provider, i);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version "+i, e);
        }
    }

    private void registerItemsUsingLegacyFormat(MappingProvider provider, int i) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + i + "/items.json")) {
            if (stream == null) {
                return;
            }
            Map<String, RegistryEntry> legacy = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), new TypeToken<Map<String, RegistryEntry>>(){}.getType());
            registerItemMappings(legacy, provider, i);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version "+i, e);
        }
    }

    private void registerItemMappings(Map<String, RegistryEntry> entries, MappingProvider provider, int i) {
        for (String type : entries.keySet()) {
            String name = type.substring("minecraft:".length()).toUpperCase();
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
        for (String type : registry.entries().keySet()) {
            String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase();
            try {
                Sound sound = Sound.valueOf(name);
                int id = registry.entries().get(type).protocolId();
                provider.registerMapping(sound, AbstractProtocolMapping.rangedIdMapping(i, i, id));
            } catch (IllegalArgumentException e) {
                log.warn("Don't know what sound " + name + " was at protocol " + i);
            }
        }
    }

    @Override
    public void registerPackets(ProtocolRegistrationProvider protocolRegistrationProvider) {

    }

}
