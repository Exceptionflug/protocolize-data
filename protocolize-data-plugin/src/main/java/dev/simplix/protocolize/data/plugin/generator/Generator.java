package dev.simplix.protocolize.data.plugin.generator;


/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public interface Generator {

    void generate() throws Exception;

    String getName();

}
