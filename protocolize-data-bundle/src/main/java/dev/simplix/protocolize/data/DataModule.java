package dev.simplix.protocolize.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.simplix.protocolize.api.Direction;
import dev.simplix.protocolize.api.PacketDirection;
import dev.simplix.protocolize.api.Protocol;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.item.component.*;
import dev.simplix.protocolize.api.mapping.AbstractProtocolMapping;
import dev.simplix.protocolize.api.module.ProtocolizeModule;
import dev.simplix.protocolize.api.providers.MappingProvider;
import dev.simplix.protocolize.api.providers.PacketListenerProvider;
import dev.simplix.protocolize.api.providers.ProtocolRegistrationProvider;
import dev.simplix.protocolize.api.util.ProtocolVersions;
import dev.simplix.protocolize.data.item.component.*;
import dev.simplix.protocolize.data.listeners.*;
import dev.simplix.protocolize.data.packets.*;
import dev.simplix.protocolize.data.registries.Registries;
import dev.simplix.protocolize.data.registries.RegistryEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * Date: 23.08.2021
 *
 * @author Exceptionflug
 */
@Slf4j(topic = "Protocolize")
public class DataModule implements ProtocolizeModule {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public void registerMappings(MappingProvider mappingProvider) {
        for (int i = ProtocolVersions.MINECRAFT_1_13; i <= ProtocolVersions.MINECRAFT_LATEST; i++) {
            registerMappingsForProtocol(mappingProvider, i);
        }
    }

    private void registerMappingsForProtocol(MappingProvider provider, int protocolVersion) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + protocolVersion + "/registries.json")) {
            if (stream == null) {
                registerItemsUsingLegacyFormat(provider, protocolVersion);
                return;
            }
            Registries registries = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Registries.class);
            registerIdMappings(registries.itemRegistry().entries(), provider, protocolVersion, ItemType.class);
            if(protocolVersion >= ProtocolVersions.MINECRAFT_1_20_5) {
                registerIdMappings(registries.mobEffectRegistry().entries(), provider, protocolVersion, MobEffect.class);
                registerIdMappings(registries.potionRegistry().entries(), provider, protocolVersion, Potion.class);
                registerIdMappings(registries.enchantmentRegistry().entries(), provider, protocolVersion, Enchantment.class);
                registerIdMappings(registries.attributeRegistry().entries(), provider, protocolVersion, AttributeType.class);
                registerIdMappings(registries.instrumentRegistry().entries(), provider, protocolVersion, Instrument.class);
            }
            registerStringMappings(registries.soundRegistry().entries(), provider, protocolVersion, Sound.class);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version {}", protocolVersion, e);
        }
    }

    private void registerItemsUsingLegacyFormat(MappingProvider provider, int protocolVersion) {
        try (InputStream stream = DataModule.class.getResourceAsStream("/registries/" + protocolVersion + "/items.json")) {
            if (stream == null) {
                return;
            }
            Map<String, RegistryEntry> legacy = this.gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), new TypeToken<Map<String, RegistryEntry>>() {
            }.getType());
            registerIdMappings(legacy, provider, protocolVersion, ItemType.class);
        } catch (Exception e) {
            log.error("Unable to register mappings for protocol version {}", protocolVersion, e);
        }
    }

    private <T extends Enum<T>> void registerIdMappings(Map<String, RegistryEntry> entries, MappingProvider provider, int protocolVersion, Class<T> enumClass) {
        for (String type : entries.keySet()) {
            String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT);
            try {
                T value = Enum.valueOf(enumClass, name);
                int id = entries.get(type).protocolId();
                provider.registerMapping(value, AbstractProtocolMapping.rangedIdMapping(protocolVersion, protocolVersion, id));
            } catch (IllegalArgumentException e) {
                log.warn("Don't know what {}: {} was at protocol {}", enumClass.getSimpleName(), name, protocolVersion);
            }
        }
    }

    private <T extends Enum<T>> void registerStringMappings(Map<String, RegistryEntry> entries, MappingProvider provider, int protocolVersion, Class<T> enumClass) {
        for (String type : entries.keySet()) {
            String name = type.substring("minecraft:".length()).replace(".", "_").toUpperCase(Locale.ROOT);
            try {
                T value = Enum.valueOf(enumClass, name);
                int protocolId = entries.get(type).protocolId();
                provider.registerMapping(value, AbstractProtocolMapping.rangedStringMapping(protocolVersion, protocolVersion, type, protocolId));
            } catch (IllegalArgumentException e) {
                log.warn("Don't know what {}: {} was at protocol {}", enumClass.getSimpleName(), name, protocolVersion);
            }
        }
    }

    @Override
    public void registerPackets(ProtocolRegistrationProvider registrationProvider) {
        if (registrationProvider == null) {
            return;
        }
        // CLIENTBOUND
        registrationProvider.registerPacket(WindowItems.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, WindowItems.class);
        registrationProvider.registerPacket(SetSlot.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, SetSlot.class);
        registrationProvider.registerPacket(HeldItemChange.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, HeldItemChange.class);
        registrationProvider.registerPacket(WindowProperty.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, WindowProperty.class);
        registrationProvider.registerPacket(OpenWindow.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, OpenWindow.class);
        registrationProvider.registerPacket(ConfirmTransaction.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, ConfirmTransaction.class);
        registrationProvider.registerPacket(CloseWindow.CLIENTBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, CloseWindow.class);
        registrationProvider.registerPacket(NamedSoundEffect.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, NamedSoundEffect.class);
        registrationProvider.registerPacket(SoundEffect.MAPPINGS, Protocol.PLAY, PacketDirection.CLIENTBOUND, SoundEffect.class);

        // SERVERBOUND
        registrationProvider.registerPacket(UseItem.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, UseItem.class);
        registrationProvider.registerPacket(HeldItemChange.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, HeldItemChange.class);
        registrationProvider.registerPacket(BlockPlacement.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, BlockPlacement.class);
        registrationProvider.registerPacket(ConfirmTransaction.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, ConfirmTransaction.class);
        registrationProvider.registerPacket(ClickWindow.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, ClickWindow.class);
        registrationProvider.registerPacket(CloseWindow.SERVERBOUND_MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, CloseWindow.class);
        registrationProvider.registerPacket(PlayerPosition.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerPosition.class);
        registrationProvider.registerPacket(PlayerPositionLook.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerPositionLook.class);
        registrationProvider.registerPacket(PlayerLook.MAPPINGS, Protocol.PLAY, PacketDirection.SERVERBOUND, PlayerLook.class);

        // ITEM STRUCTURED COMPONENTS
        Protocolize.registerService(AttributeModifiersComponent.Factory.class, AttributeModifiersComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(AttributeModifiersComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BannerPatternsComponent.Factory.class, BannerPatternsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BannerPatternsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BaseColorComponent.Factory.class, BaseColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BaseColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BeesComponent.Factory.class, BeesComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BeesComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BlockEntityDataComponent.Factory.class, BlockEntityDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BlockEntityDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BlockStateComponent.Factory.class, BlockStateComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BlockStateComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BucketEntityDataComponent.Factory.class, BucketEntityDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BucketEntityDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(BundleContentsComponent.Factory.class, BundleContentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(BundleContentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CanBreakComponent.Factory.class, CanBreakComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(CanBreakComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CanPlaceOnComponent.Factory.class, CanPlaceOnComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(CanPlaceOnComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ChargedProjectilesComponent.Factory.class, ChargedProjectilesComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(ChargedProjectilesComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ContainerComponent.Factory.class, ContainerComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(ContainerComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ContainerLootComponent.Factory.class, ContainerLootComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(ContainerLootComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CreativeSlotLockComponent.Factory.class, CreativeSlotLockComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(CreativeSlotLockComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CustomDataComponent.Factory.class, CustomDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(CustomDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CustomModelDataComponent.Factory.class, CustomModelDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(CustomModelDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(CustomNameComponent.Factory.class, CustomNameComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(CustomNameComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DamageComponent.Factory.class, DamageComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(DamageComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DebugStickStateComponent.Factory.class, DebugStickStateComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(DebugStickStateComponentImpl.Type.INSTANCE);

        Protocolize.registerService(DyedColorComponent.Factory.class, DyedColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(DyedColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EnchantmentGlintComponent.Factory.class, EnchantmentGlintComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(EnchantmentGlintComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EnchantmentsComponent.Factory.class, EnchantmentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(EnchantmentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(EntityDataComponent.Factory.class, EntityDataComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(EntityDataComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FireResistantComponent.Factory.class, FireResistantComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(FireResistantComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FireworkExplosionComponent.Factory.class, FireworkExplosionComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(FireworkExplosionComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FireworksComponent.Factory.class, FireworksComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(FireworksComponentImpl.Type.INSTANCE);

        Protocolize.registerService(FoodComponent.Factory.class, FoodComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(FoodComponentImpl.Type.INSTANCE);

        Protocolize.registerService(HideAdvancedTooltipComponent.Factory.class, HideAdditionalTooltipComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(HideAdditionalTooltipComponentImpl.Type.INSTANCE);

        Protocolize.registerService(HideTooltipComponent.Factory.class, HideTooltipComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(HideTooltipComponentImpl.Type.INSTANCE);

        Protocolize.registerService(InstrumentComponent.Factory.class, InstrumentComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(InstrumentComponentImpl.Type.INSTANCE);

        Protocolize.registerService(IntangibleProjectileComponent.Factory.class, IntangibleProjectileComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(IntangibleProjectileComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ItemNameComponent.Factory.class, ItemNameComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(ItemNameComponentImpl.Type.INSTANCE);

        Protocolize.registerService(JukeboxPlayableComponent.Factory.class, JukeboxPlayableComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(JukeboxPlayableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LockComponent.Factory.class, LockComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(LockComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LodestoneTrackerComponent.Factory.class, LodestoneTrackerComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(LodestoneTrackerComponentImpl.Type.INSTANCE);

        Protocolize.registerService(LoreComponent.Factory.class, LoreComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(LoreComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapColorComponent.Factory.class, MapColorComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(MapColorComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapDecorationsComponent.Factory.class, MapDecorationsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(MapDecorationsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapIdComponent.Factory.class, MapIdComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(MapIdComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MapPostProcessingComponent.Factory.class, MapPostProcessingComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(MapPostProcessingComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MaxDamageComponent.Factory.class, MaxDamageComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(MaxDamageComponentImpl.Type.INSTANCE);

        Protocolize.registerService(MaxStackSizeComponent.Factory.class, MaxStackSizeComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(MaxStackSizeComponentImpl.Type.INSTANCE);

        Protocolize.registerService(NoteBlockSoundComponent.Factory.class, NoteBlockSoundComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(NoteBlockSoundComponentImpl.Type.INSTANCE);

        Protocolize.registerService(OminousBottleAmplifierComponent.Factory.class, OminousBottleAmplifierComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(OminousBottleAmplifierComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PotDecorationsComponent.Factory.class, PotDecorationsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(PotDecorationsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(PotionContentsComponent.Factory.class, PotionContentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(PotionContentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ProfileComponent.Factory.class, ProfileComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(ProfileComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RarityComponent.Factory.class, RarityComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(RarityComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RecipesComponent.Factory.class, RecipesComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(RecipesComponentImpl.Type.INSTANCE);

        Protocolize.registerService(RepairCostComponent.Factory.class, RepairCostComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(RepairCostComponentImpl.Type.INSTANCE);

        Protocolize.registerService(StoredEnchantmentsComponent.Factory.class, StoredEnchantmentsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(StoredEnchantmentsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(SuspiciousStewEffectsComponent.Factory.class, SuspiciousStewEffectsComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(SuspiciousStewEffectsComponentImpl.Type.INSTANCE);

        Protocolize.registerService(ToolComponent.Factory.class, ToolComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(ToolComponentImpl.Type.INSTANCE);

        Protocolize.registerService(TrimComponent.Factory.class, TrimComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(TrimComponentImpl.Type.INSTANCE);

        Protocolize.registerService(UnbreakableComponent.Factory.class, UnbreakableComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(UnbreakableComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WritableBookContentComponent.Factory.class, WritableBookContentComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(WritableBookContentComponentImpl.Type.INSTANCE);

        Protocolize.registerService(WrittenBookContentComponent.Factory.class, WrittenBookContentComponentImpl.Type.INSTANCE);
        registrationProvider.registerItemStructuredComponentType(WrittenBookContentComponentImpl.Type.INSTANCE);

        PacketListenerProvider listenerProvider = Protocolize.listenerProvider();
        listenerProvider.registerListener(new CloseWindowListener(Direction.UPSTREAM));
        listenerProvider.registerListener(new CloseWindowListener(Direction.DOWNSTREAM));
        listenerProvider.registerListener(new ClickWindowListener());
        listenerProvider.registerListener(new PlayerPositionListener());
        listenerProvider.registerListener(new PlayerPositionLookListener());
        listenerProvider.registerListener(new PlayerLookListener());
        listenerProvider.registerListener(new UseItemListener());
        listenerProvider.registerListener(new BlockPlacementListener());
    }


}
