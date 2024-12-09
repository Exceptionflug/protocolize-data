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
    private GenericRegistry soundRegistry;

    @SerializedName("minecraft:mob_effect")
    private GenericRegistry mobEffectRegistry;

    @SerializedName("minecraft:potion")
    private GenericRegistry potionRegistry;

    @SerializedName("minecraft:enchantment")
    private GenericRegistry enchantmentRegistry;

    @SerializedName("minecraft:attribute")
    private GenericRegistry attributeRegistry;

    @SerializedName("minecraft:instrument")
    private GenericRegistry instrumentRegistry;

    @SerializedName("minecraft:data_component_type")
    private GenericRegistry dataComponentTypeRegistry;

    @SerializedName("minecraft:entity_type")
    private GenericRegistry entityTypeRegistry;

    @SerializedName("minecraft:consume_effect_type")
    private GenericRegistry consumeEffectTypeRegistry;

    @SerializedName("minecraft:block")
    private GenericRegistry blockRegistry;

}
