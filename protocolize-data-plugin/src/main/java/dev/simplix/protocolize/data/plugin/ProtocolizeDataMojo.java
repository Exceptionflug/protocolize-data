package dev.simplix.protocolize.data.plugin;

import com.google.gson.Gson;
import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.plugin.generators.GenericGenerator;
import dev.simplix.protocolize.data.registries.GenericRegistry;
import dev.simplix.protocolize.data.registries.Registries;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.simplix.protocolize.api.util.ProtocolVersions.*;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Mojo(name = "compile")
public class ProtocolizeDataMojo extends AbstractMojo {

    private static final File CLASSES_DIR = new File("protocolize-data-bundle/target/classes/");
    private static final Gson GSON = new Gson();
    private static final int protocolStartForDataComponents = MINECRAFT_1_20_5;
    private static final Map<Integer, Registries> parsedRegistries = new HashMap<>();

    @SneakyThrows
    @Override
    public void execute() {
        Registries latestRegistries = getRegistry(MINECRAFT_LATEST);

        List<Generator> generators = new ArrayList<>(Arrays.asList(
            new GenericGenerator(CLASSES_DIR, latestRegistries.itemRegistry(), "ItemType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.soundRegistry(), "Sound"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.mobEffectRegistry(), "MobEffect"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.potionRegistry(), "Potion"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.attributeRegistry(), "AttributeType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.consumeEffectTypeRegistry(), "ConsumeEffectType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.entityTypeRegistry(), "EntityType"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.blockRegistry(), "Block"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.enchantmentRegistry(), "Enchantment"),
            new GenericGenerator(CLASSES_DIR, latestRegistries.instrumentRegistry(), "Instrument")
        ));


        /*
         * This will register all DataComponentType's for all versions 1.20.5-Latest as some were already removed.
         * It will still use the mapping provider to grab correct versions or warn if they cannot be used on a
         * specific version.
         */
        List<GenericRegistry> dataComponentRegistries = new ArrayList<>();
        for(Registries registry : getRegistries(MINECRAFT_1_20_5, MINECRAFT_LATEST)){
            try {
                dataComponentRegistries.add(registry.dataComponentTypeRegistry());
            } catch (Exception ignored) { }
        }
        generators.add(new GenericGenerator(CLASSES_DIR, dataComponentRegistries, "DataComponentType"));

        for (Generator generator : generators) {
            long now = System.currentTimeMillis();
            getLog().info("Running generator for " + generator.getName() + "...");
            generator.generate();
            getLog().info("Generator for " + generator.getName() + " done (" + (System.currentTimeMillis() - now) + " ms)");
        }
    }

    private Registries getRegistry(int protocolVersion) throws FileNotFoundException {
        if(parsedRegistries.containsKey(protocolVersion)){
            return parsedRegistries.get(protocolVersion);
        } else {
            try {
                Registries registry = GSON.fromJson(new FileReader("protocolize-data-bundle/src/main/resources/registries/" + protocolVersion + "/registries.json"), Registries.class);
                parsedRegistries.put(protocolVersion, registry);
                return registry;
            } catch (Exception ignored){
                getLog().warn("Failed to get registry for version " + protocolVersion);
            }
        }
        return null;
    }

    private List<Registries> getRegistries(int startVersion, int endVersion) throws FileNotFoundException {
        List<Registries> registries = new ArrayList<>();
        for(int i = startVersion; i <= endVersion; i++){
            Registries registry = getRegistry(i);
            if(registry != null){
                registries.add(registry);
            }
        }
        return registries;
    }

}
