package dev.simplix.protocolize.data.plugin.generator;

import dev.simplix.protocolize.data.registries.Registries;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public interface Generator {

    void generate(Registries registries) throws Exception;

}
