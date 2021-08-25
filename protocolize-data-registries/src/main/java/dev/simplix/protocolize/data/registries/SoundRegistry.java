package dev.simplix.protocolize.data.registries;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Getter
@Accessors(fluent = true)
public class SoundRegistry {

    private Map<String, RegistryEntry> entries;

}
