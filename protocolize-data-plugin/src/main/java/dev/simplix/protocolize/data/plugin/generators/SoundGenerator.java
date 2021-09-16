package dev.simplix.protocolize.data.plugin.generators;

import dev.simplix.protocolize.data.plugin.generator.Generator;
import dev.simplix.protocolize.data.registries.Registries;
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
public class SoundGenerator implements Generator {

    private final File target;

    public SoundGenerator(File target) {
        this.target = target;
        target.getParentFile().mkdirs();
    }

    @Override
    public void generate(Registries registries) throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V8).makeEnumeration(registries.soundRegistry().entries().keySet()
                .stream().map(s -> s.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT)).collect(Collectors.toList()))
            .name("dev.simplix.protocolize.data.Sound")
            .make()
            .saveIn(target);
    }

}
