package dev.simplix.protocolize.data.plugin.generators;

import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.registries.GenericRegistry;
import lombok.Getter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;

import java.io.File;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
public class GenericGenerator implements Generator {

    private final File target;
    @Getter
    private final String name;
    private final GenericRegistry registry;

    public GenericGenerator(File target, GenericRegistry registry, String name) {
        this.target = target;
        this.registry = registry;
        this.name = name;
        target.getParentFile().mkdirs();
    }

    @Override
    public void generate() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V8).makeEnumeration(registry.entries().keySet()
                .stream().map(s -> s.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT)).collect(Collectors.toList()))
            .name("dev.simplix.protocolize.data." + name)
            .make()
            .saveIn(target);
    }

}
