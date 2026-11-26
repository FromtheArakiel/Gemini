package dev.arakiel.gemini.event;

import dev.arakiel.gemini.event.client.ClientChatEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventCatalogTest {
    private static final List<String> EVENT_TYPES = List.of(
            "dev.arakiel.gemini.event.command.RegisterCommandsEvent",
            "dev.arakiel.gemini.event.command.CommandEvent",
            "dev.arakiel.gemini.event.resource.AddReloadListenerEvent",
            "dev.arakiel.gemini.event.resource.AddPackFindersEvent",
            "dev.arakiel.gemini.event.resource.DatapackSyncEvent",
            "dev.arakiel.gemini.event.resource.TagsUpdatedEvent",
            "dev.arakiel.gemini.event.resource.LootTableLoadEvent",
            "dev.arakiel.gemini.event.server.PlayerLoggedInEvent",
            "dev.arakiel.gemini.event.server.PlayerLoggedOutEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerCloneEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerRespawnEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerChangedDimensionEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerContainerEvent",
            "dev.arakiel.gemini.event.entity.player.AdvancementEarnEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerDestroyItemEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerTickEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerXpPickupEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerBreakBlockEvent",
            "dev.arakiel.gemini.event.block.BlockDropsEvent",
            "dev.arakiel.gemini.event.block.EntityPlaceEvent",
            "dev.arakiel.gemini.event.block.FarmlandTrampleEvent",
            "dev.arakiel.gemini.event.explosion.ExplosionStartEvent",
            "dev.arakiel.gemini.event.explosion.ExplosionDetonateEvent",
            "dev.arakiel.gemini.event.explosion.ExplosionKnockbackEvent",
            "dev.arakiel.gemini.event.block.PistonEvent",
            "dev.arakiel.gemini.event.block.CropGrowEvent",
            "dev.arakiel.gemini.event.block.CreateFluidSourceEvent",
            "dev.arakiel.gemini.event.block.BlockGrowFeatureEvent",
            "dev.arakiel.gemini.event.spawn.ModifyCustomSpawnersEvent",
            "dev.arakiel.gemini.event.spawn.FinalizeSpawnEvent",
            "dev.arakiel.gemini.event.entity.living.LivingDropsEvent",
            "dev.arakiel.gemini.event.entity.living.LivingDeathEvent",
            "dev.arakiel.gemini.event.entity.living.LivingDamageEvent",
            "dev.arakiel.gemini.event.entity.living.LivingIncomingDamageEvent",
            "dev.arakiel.gemini.event.entity.living.LivingExperienceDropEvent",
            "dev.arakiel.gemini.event.entity.living.LivingConversionEvent",
            "dev.arakiel.gemini.event.spawn.MobSpawnEvent",
            "dev.arakiel.gemini.event.spawn.MobDespawnEvent",
            "dev.arakiel.gemini.event.entity.EntityAttributeModificationEvent",
            "dev.arakiel.gemini.event.spawn.RegisterSpawnPlacementsEvent",
            "dev.arakiel.gemini.event.item.ItemTossEvent",
            "dev.arakiel.gemini.event.item.ItemEntityPickupEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerItemCraftedEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerItemSmeltedEvent",
            "dev.arakiel.gemini.event.entity.player.ItemStackedOnOtherEvent",
            "dev.arakiel.gemini.event.entity.player.ItemAttributeModifierEvent",
            "dev.arakiel.gemini.event.item.ModifyDefaultComponentsEvent",
            "dev.arakiel.gemini.event.block.FurnaceFuelBurnTimeEvent",
            "dev.arakiel.gemini.event.client.ClientPlayerNetworkEvent",
            "dev.arakiel.gemini.event.client.ClientTickEvent",
            "dev.arakiel.gemini.event.client.CustomizeGuiOverlayEvent",
            "dev.arakiel.gemini.event.client.ScreenEvent",
            "dev.arakiel.gemini.event.client.ContainerScreenEvent",
            "dev.arakiel.gemini.event.client.ItemTooltipEvent",
            "dev.arakiel.gemini.event.client.RenderTooltipEvent",
            "dev.arakiel.gemini.event.client.RegisterMenuScreensEvent",
            "dev.arakiel.gemini.event.client.RegisterKeyMappingsEvent",
            "dev.arakiel.gemini.event.client.RegisterParticleProvidersEvent",
            "dev.arakiel.gemini.event.client.EntityRenderersEvent",
            "dev.arakiel.gemini.event.enchant.AnvilUpdateEvent",
            "dev.arakiel.gemini.event.enchant.AnvilRepairEvent",
            "dev.arakiel.gemini.event.enchant.GrindstoneEvent",
            "dev.arakiel.gemini.event.enchant.EnchantmentLevelSetEvent",
            "dev.arakiel.gemini.event.enchant.GetEnchantmentLevelEvent",
            "dev.arakiel.gemini.event.enchant.PlayerEnchantItemEvent",
            "dev.arakiel.gemini.event.brewing.RegisterBrewingRecipesEvent",
            "dev.arakiel.gemini.event.brewing.PlayerBrewedPotionEvent",
            "dev.arakiel.gemini.event.brewing.PotionBrewEvent",
            "dev.arakiel.gemini.event.trade.VillagerTradesEvent",
            "dev.arakiel.gemini.event.trade.WandererTradesEvent",
            "dev.arakiel.gemini.event.trade.TradeWithVillagerEvent",
            "dev.arakiel.gemini.event.entity.living.AnimalTameEvent",
            "dev.arakiel.gemini.event.entity.living.BabyEntitySpawnEvent",
            "dev.arakiel.gemini.event.entity.living.MobSplitEvent",
            "dev.arakiel.gemini.event.entity.living.LivingShieldBlockEvent",
            "dev.arakiel.gemini.event.entity.living.LivingUseTotemEvent",
            "dev.arakiel.gemini.event.entity.living.LivingFallEvent",
            "dev.arakiel.gemini.event.entity.living.LivingBreatheEvent",
            "dev.arakiel.gemini.event.entity.living.LivingDrownEvent",
            "dev.arakiel.gemini.event.entity.living.LivingChangeTargetEvent",
            "dev.arakiel.gemini.event.entity.living.LivingDestroyBlockEvent",
            "dev.arakiel.gemini.event.entity.living.LivingSwapItemsEvent",
            "dev.arakiel.gemini.event.entity.living.LivingGetProjectileEvent",
            "dev.arakiel.gemini.event.entity.living.ArmorHurtEvent",
            "dev.arakiel.gemini.event.entity.EntityInvulnerabilityCheckEvent",
            "dev.arakiel.gemini.event.entity.EntityMountEvent",
            "dev.arakiel.gemini.event.entity.EntityTeleportEvent",
            "dev.arakiel.gemini.event.entity.EntityTravelToDimensionEvent",
            "dev.arakiel.gemini.event.entity.EntityStruckByLightningEvent",
            "dev.arakiel.gemini.event.entity.EntityMobGriefingEvent",
            "dev.arakiel.gemini.event.chunk.ChunkEvent",
            "dev.arakiel.gemini.event.chunk.ChunkDataEvent",
            "dev.arakiel.gemini.event.chunk.ChunkWatchEvent",
            "dev.arakiel.gemini.event.world.NoteBlockEvent",
            "dev.arakiel.gemini.event.world.SleepFinishedTimeEvent",
            "dev.arakiel.gemini.event.world.CanPlayerSleepEvent",
            "dev.arakiel.gemini.event.world.CanContinueSleepingEvent",
            "dev.arakiel.gemini.event.world.PlayerSetSpawnEvent",
            "dev.arakiel.gemini.event.world.PlayerWakeUpEvent",
            "dev.arakiel.gemini.event.entity.player.PlayerNegotiationEvent",
            "dev.arakiel.gemini.event.combat.CriticalHitEvent",
            "dev.arakiel.gemini.event.combat.SweepAttackEvent",
            "dev.arakiel.gemini.event.projectile.ArrowNockEvent",
            "dev.arakiel.gemini.event.projectile.ArrowLooseEvent",
            "dev.arakiel.gemini.event.entity.ProjectileImpactEvent",
            "dev.arakiel.gemini.event.level.EntityJoinLevelEvent",
            "dev.arakiel.gemini.event.level.EntityLeaveLevelEvent",
            "dev.arakiel.gemini.event.client.RenderGuiEvent",
            "dev.arakiel.gemini.event.client.RenderGuiLayerEvent",
            "dev.arakiel.gemini.event.client.RenderLevelStageEvent",
            "dev.arakiel.gemini.event.client.RenderHighlightEvent",
            "dev.arakiel.gemini.event.client.RegisterColorHandlersEvent",
            "dev.arakiel.gemini.event.client.RegisterEntityRenderersEvent",
            "dev.arakiel.gemini.event.client.TextureAtlasStitchedEvent",
            "dev.arakiel.gemini.event.client.RecipesUpdatedEvent",
            "dev.arakiel.gemini.event.client.ClientChatEvent",
            "dev.arakiel.gemini.event.client.ClientChatReceivedEvent",
            "dev.arakiel.gemini.event.client.RegisterClientCommandsEvent",
            "dev.arakiel.gemini.event.client.RegisterClientReloadListenersEvent",
            "dev.arakiel.gemini.event.client.RegisterRecipeBookCategoriesEvent",
            "dev.arakiel.gemini.event.client.RegisterShadersEvent",
            "dev.arakiel.gemini.event.client.ModelEvent"
    );

    @Test
    void everyListedEventExistsAndExtendsGeminiEvent() {
        for (String typeName : EVENT_TYPES) {
            Class<?> type = load(typeName);
            assertTrue(
                    GeminiEvent.class.isAssignableFrom(type),
                    typeName + " must extend GeminiEvent"
            );
        }
    }

    @Test
    void phaseBasedEventsExposePhaseEnums() {
        List<String> phaseEvents = List.of(
                "dev.arakiel.gemini.event.entity.living.LivingDamageEvent",
                "dev.arakiel.gemini.event.entity.living.LivingConversionEvent",
                "dev.arakiel.gemini.event.entity.player.PlayerContainerEvent",
                "dev.arakiel.gemini.event.entity.player.PlayerItemPickupEvent",
                "dev.arakiel.gemini.event.entity.player.PlayerTickEvent",
                "dev.arakiel.gemini.event.item.ItemEntityPickupEvent",
                "dev.arakiel.gemini.event.spawn.MobSpawnEvent",
                "dev.arakiel.gemini.event.block.PistonEvent",
                "dev.arakiel.gemini.event.block.CropGrowEvent",
                "dev.arakiel.gemini.event.chunk.ChunkEvent",
                "dev.arakiel.gemini.event.chunk.ChunkDataEvent",
                "dev.arakiel.gemini.event.chunk.ChunkWatchEvent",
                "dev.arakiel.gemini.event.world.NoteBlockEvent",
                "dev.arakiel.gemini.event.client.ClientPlayerNetworkEvent",
                "dev.arakiel.gemini.event.client.RenderGuiEvent",
                "dev.arakiel.gemini.event.client.RenderGuiLayerEvent",
                "dev.arakiel.gemini.event.client.RenderHighlightEvent",
                "dev.arakiel.gemini.event.client.CustomizeGuiOverlayEvent",
                "dev.arakiel.gemini.event.client.RenderTooltipEvent",
                "dev.arakiel.gemini.event.client.ModelEvent"
        );
        for (String typeName : phaseEvents) {
            Class<?> type = load(typeName);
            assertTrue(hasPhaseEnum(type), typeName + " must expose Phase");
        }
    }

    @Test
    void resultEventsUseHasResult() {
        List<String> resultEvents = List.of(
                "dev.arakiel.gemini.event.block.CreateFluidSourceEvent",
                "dev.arakiel.gemini.event.effect.MobEffectApplicableEvent",
                "dev.arakiel.gemini.event.world.CanPlayerSleepEvent"
        );
        for (String typeName : resultEvents) {
            assertTrue(load(typeName).isAnnotationPresent(HasResult.class), typeName);
        }
    }

    @Test
    void cancelableCatalogEventsUseCancelableMetadata() {
        List<String> cancelableEvents = List.of(
                "dev.arakiel.gemini.event.enchant.AnvilUpdateEvent",
                "dev.arakiel.gemini.event.enchant.GrindstoneEvent$OnPlaceItem",
                "dev.arakiel.gemini.event.enchant.GrindstoneEvent$OnTakeItem",
                "dev.arakiel.gemini.event.enchant.EnchantmentLevelSetEvent",
                "dev.arakiel.gemini.event.enchant.GetEnchantmentLevelEvent",
                "dev.arakiel.gemini.event.enchant.PlayerEnchantItemEvent",
                "dev.arakiel.gemini.event.brewing.PotionBrewEvent",
                "dev.arakiel.gemini.event.trade.TradeWithVillagerEvent",
                "dev.arakiel.gemini.event.entity.living.AnimalTameEvent",
                "dev.arakiel.gemini.event.entity.living.BabyEntitySpawnEvent",
                "dev.arakiel.gemini.event.entity.living.MobSplitEvent",
                "dev.arakiel.gemini.event.entity.living.LivingShieldBlockEvent",
                "dev.arakiel.gemini.event.entity.living.LivingUseTotemEvent",
                "dev.arakiel.gemini.event.entity.living.LivingFallEvent",
                "dev.arakiel.gemini.event.entity.living.LivingDrownEvent",
                "dev.arakiel.gemini.event.entity.living.LivingChangeTargetEvent",
                "dev.arakiel.gemini.event.entity.living.LivingDestroyBlockEvent",
                "dev.arakiel.gemini.event.entity.living.LivingSwapItemsEvent",
                "dev.arakiel.gemini.event.entity.living.LivingGetProjectileEvent",
                "dev.arakiel.gemini.event.entity.living.ArmorHurtEvent",
                "dev.arakiel.gemini.event.entity.EntityMountEvent",
                "dev.arakiel.gemini.event.entity.EntityTeleportEvent",
                "dev.arakiel.gemini.event.entity.EntityTravelToDimensionEvent",
                "dev.arakiel.gemini.event.entity.EntityStruckByLightningEvent",
                "dev.arakiel.gemini.event.world.NoteBlockEvent",
                "dev.arakiel.gemini.event.world.PlayerSetSpawnEvent",
                "dev.arakiel.gemini.event.entity.player.PlayerNegotiationEvent",
                "dev.arakiel.gemini.event.combat.SweepAttackEvent",
                "dev.arakiel.gemini.event.projectile.ArrowNockEvent",
                "dev.arakiel.gemini.event.projectile.ArrowLooseEvent",
                "dev.arakiel.gemini.event.client.RenderGuiEvent",
                "dev.arakiel.gemini.event.client.RenderHighlightEvent",
                "dev.arakiel.gemini.event.client.ClientChatEvent",
                "dev.arakiel.gemini.event.client.ClientChatReceivedEvent",
                "dev.arakiel.gemini.event.client.RenderGuiLayerEvent$Pre",
                "dev.arakiel.gemini.event.client.RenderTooltipEvent$Pre",
                "dev.arakiel.gemini.event.client.RenderTooltipEvent$GatherComponents"
        );
        for (String typeName : cancelableEvents) {
            assertTrue(load(typeName).isAnnotationPresent(Cancelable.class), typeName);
        }
    }

    @Test
    void simpleChatEventPreservesMutableSemanticsAndCancellation() {
        ClientChatEvent event = new ClientChatEvent("first");
        event.setMessage("second");
        event.setCanceled(true);

        assertTrue(event.isCanceled());
        assertTrue(event.isCancelable());
        assertTrue(event.getMessage().equals("second"));
        assertTrue(event.getOriginalMessage().equals("first"));
    }

    private static boolean hasPhaseEnum(Class<?> type) {
        for (Class<?> nested : type.getDeclaredClasses()) {
            if (nested.getSimpleName().equals("Phase") && nested.isEnum()) {
                return true;
            }
        }
        return false;
    }

    private static Class<?> load(String typeName) {
        try {
            return Class.forName(typeName, false, EventCatalogTest.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new AssertionError("Missing event type " + typeName, exception);
        }
    }
}
