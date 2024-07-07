package dev.simplix.protocolize.data.plugin;

import com.google.gson.Gson;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.plugin.generators.GenericGenerator;
import dev.simplix.protocolize.data.registries.Registries;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Mojo(name = "compile")
public class ProtocolizeDataMojo extends AbstractMojo {

    private static final File CLASSES_DIR = new File("protocolize-data-bundle/target/classes/");
    private static final Gson GSON = new Gson();

    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Registries latestRegistries = parseRegistries(ProtocolVersions.MINECRAFT_LATEST);

        List<Generator> generators = Arrays.asList(
            new GenericGenerator(CLASSES_DIR, latestRegistries.itemRegistry(), "ItemType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.soundRegistry(), "Sound"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.mobEffectRegistry(), "MobEffect"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.potionRegistry(), "Potion"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.enchantmentRegistry(), "Enchantment"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.attributeRegistry(), "AttributeType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.instrumentRegistry(), "Instrument")
        );

        for (Generator generator : generators) {
            long now = System.currentTimeMillis();
            getLog().info("Running generator for " + generator.getName() + "...");
            generator.generate();
            getLog().info("Generator for " + generator.getName() + " done (" + (System.currentTimeMillis() - now) + " ms)");
        }
    }

    private Registries parseRegistries(int protocolVersion) throws FileNotFoundException {
        return GSON.fromJson(new FileReader("protocolize-data-bundle/src/main/resources/registries/" + protocolVersion + "/registries.json"), Registries.class);
    }

}
