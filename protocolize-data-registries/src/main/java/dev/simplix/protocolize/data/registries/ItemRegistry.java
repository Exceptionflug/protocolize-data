package dev.simplix.protocolize.data.registries;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Accessors(fluent = true)
public class ItemRegistry {

    @SerializedName("default")
    private String defaultItem;
    private Map<String, RegistryEntry> entries;

}