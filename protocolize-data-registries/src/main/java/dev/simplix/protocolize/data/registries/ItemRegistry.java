package dev.simplix.protocolize.data.registries;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class ItemRegistry extends GenericRegistry {

    @SerializedName("default")
    private String defaultItem;

}
