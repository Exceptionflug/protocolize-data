package dev.simplix.protocolize.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class SoundEffectData {

    private final Sound sound;

    public SoundEffectData(Sound sound) {
        this.sound = sound;
    }

}
