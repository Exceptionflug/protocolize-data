package dev.simplix.protocolize.data.plugin;

import com.google.gson.Gson;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.plugin.generators.ItemTypeGenerator;
import dev.simplix.protocolize.data.plugin.generators.SoundGenerator;
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

    private final List<Generator> generators = Arrays.asList(
            new ItemTypeGenerator(CLASSES_DIR),
            new SoundGenerator(CLASSES_DIR)
    );

    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Registries latestRegistries = parseRegistries(ProtocolVersions.MINECRAFT_LATEST);
        for (Generator generator : generators) {
            long now = System.currentTimeMillis();
            getLog().info("Running " + generator.getClass().getName() + "...");
            generator.generate(latestRegistries);
            getLog().info("Running " + generator.getClass().getName() + " done (" + (System.currentTimeMillis() - now) +" ms)");
        }
    }

    private Registries parseRegistries(int protocolVersion) throws FileNotFoundException {
        return GSON.fromJson(new FileReader("protocolize-data-bundle/src/main/resources/registries/" + protocolVersion + "/registries.json"), Registries.class);
    }

}
