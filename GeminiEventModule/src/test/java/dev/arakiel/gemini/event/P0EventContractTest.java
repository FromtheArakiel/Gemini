package dev.arakiel.gemini.event;

import dev.arakiel.gemini.event.block.BlockDropsEvent;
import dev.arakiel.gemini.event.block.BlockGrowFeatureEvent;
import dev.arakiel.gemini.event.block.CreateFluidSourceEvent;
import dev.arakiel.gemini.event.block.CropGrowEvent;
import dev.arakiel.gemini.event.block.EntityPlaceEvent;
import dev.arakiel.gemini.event.block.FarmlandTrampleEvent;
import dev.arakiel.gemini.event.block.PistonEvent;
import dev.arakiel.gemini.event.client.ContainerScreenEvent;
import dev.arakiel.gemini.event.client.ItemTooltipEvent;
import dev.arakiel.gemini.event.client.RenderTooltipEvent;
import dev.arakiel.gemini.event.client.ScreenEvent;
import dev.arakiel.gemini.event.command.CommandEvent;
import dev.arakiel.gemini.event.entity.ProjectileImpactEvent;
import dev.arakiel.gemini.event.entity.living.LivingDamageEvent;
import dev.arakiel.gemini.event.entity.living.LivingDeathEvent;
import dev.arakiel.gemini.event.entity.living.LivingDropsEvent;
import dev.arakiel.gemini.event.entity.living.LivingConversionEvent;
import dev.arakiel.gemini.event.entity.living.LivingExperienceDropEvent;
import dev.arakiel.gemini.event.entity.living.LivingHurtEvent;
import dev.arakiel.gemini.event.entity.living.LivingIncomingDamageEvent;
import dev.arakiel.gemini.event.entity.player.PlayerBreakBlockEvent;
import dev.arakiel.gemini.event.entity.player.PlayerItemPickupEvent;
import dev.arakiel.gemini.event.entity.player.PlayerPlaceBlockEvent;
import dev.arakiel.gemini.event.entity.player.PlayerXpPickupEvent;
import dev.arakiel.gemini.event.explosion.ExplosionDetonateEvent;
import dev.arakiel.gemini.event.explosion.ExplosionKnockbackEvent;
import dev.arakiel.gemini.event.explosion.ExplosionStartEvent;
import dev.arakiel.gemini.event.item.ItemEntityPickupEvent;
import dev.arakiel.gemini.event.item.ItemTossEvent;
import dev.arakiel.gemini.event.entity.player.ItemStackedOnOtherEvent;
import dev.arakiel.gemini.event.spawn.MobSpawnEvent;
import dev.arakiel.gemini.event.spawn.MobDespawnEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class P0EventContractTest {
    @Test
    void cancelableEventsAdvertiseTheirContract() {
        List<Class<? extends GeminiEvent>> cancelable = List.of(
                LivingDeathEvent.class,
                LivingDamageEvent.class,
                LivingHurtEvent.class,
                LivingIncomingDamageEvent.class,
                LivingDropsEvent.class,
                LivingConversionEvent.class,
                LivingExperienceDropEvent.class,
                PlayerBreakBlockEvent.class,
                PlayerPlaceBlockEvent.class,
                PlayerItemPickupEvent.class,
                PlayerXpPickupEvent.class,
                ProjectileImpactEvent.class,
                ItemEntityPickupEvent.class,
                ItemTossEvent.class,
                ItemStackedOnOtherEvent.class,
                PistonEvent.class,
                BlockDropsEvent.class,
                BlockGrowFeatureEvent.class,
                CropGrowEvent.class,
                EntityPlaceEvent.class,
                FarmlandTrampleEvent.class,
                ExplosionStartEvent.class,
                ExplosionDetonateEvent.class,
                ExplosionKnockbackEvent.class,
                MobSpawnEvent.class,
                MobDespawnEvent.class,
                CommandEvent.class,
                ScreenEvent.class,
                ContainerScreenEvent.class,
                ItemTooltipEvent.class,
                RenderTooltipEvent.class
        );

        for (Class<? extends GeminiEvent> type : cancelable) {
            assertTrue(type.isAnnotationPresent(Cancelable.class), type.getName());
        }
    }

    @Test
    void resultEventAdvertisesResultContract() {
        assertTrue(CreateFluidSourceEvent.class.isAnnotationPresent(HasResult.class));
    }

    @Test
    void ordinaryNotificationEventIsNotCancelable() {
        assertFalse(dev.arakiel.gemini.event.resource.TagsUpdatedEvent.class.isAnnotationPresent(Cancelable.class));
    }
}
