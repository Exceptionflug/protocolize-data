package dev.simplix.protocolize.data.registries;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class Registries {

    @SerializedName("minecraft:item")
    private ItemRegistry itemRegistry;
    @SerializedName("minecraft:sound_event")
    private SoundRegistry soundRegistry;

}