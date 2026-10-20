package dev.arakiel.gemini.component.internal.item;

import dev.arakiel.gemini.component.item.ItemComponentInitializer;
import dev.arakiel.gemini.component.item.ItemComponentMigrationRegistry;
import dev.arakiel.gemini.component.internal.base.ComponentsInternals;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix.ItemStackData;

public final class StaticItemComponentPlugin implements ItemComponentMigrationRegistry, ModInitializer {
   public static final StaticItemComponentPlugin INSTANCE = new StaticItemComponentPlugin();
   private final Map<ResourceLocation, DataComponentType<?>> migrations = new HashMap<>();

   private StaticItemComponentPlugin() {
   }

   @Override
   public void registerMigration(ResourceLocation oldComponentId, DataComponentType<?> mcComponentType) {
      if (this.migrations.put(oldComponentId, mcComponentType) != null) {
         ComponentsInternals.LOGGER.warn("[Gemini] Overwriting component migration for {}", oldComponentId);
      }
   }

   public void migrate(ItemStackData data) {
      for (Entry<ResourceLocation, DataComponentType<?>> entry : this.migrations.entrySet()) {
         String oldComponentId = entry.getKey().toString();
         String mcComponentId = BuiltInRegistries.DATA_COMPONENT_TYPE
            .getResourceKey(entry.getValue())
            .orElseThrow(
               () -> new IllegalStateException(
                     "Registered migration for component " + oldComponentId + " towards unregistered item component type " + entry.getValue()
                  )
            )
            .location()
            .toString();
         data.moveTagToComponent(oldComponentId, mcComponentId);
      }
   }

   public void onInitialize() {
      StaticComponentPluginBase.processInitializers(
         StaticComponentPluginBase.getComponentEntrypoints("gemini:components-item", ItemComponentInitializer.class),
         initializer -> initializer.registerItemComponentMigrations(this)
      );
   }
}
