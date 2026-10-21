package dev.arakiel.gemini.mixin.component.world;

import com.mojang.datafixers.util.Unit;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.ComponentSync;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.world.GeminiWorldComponents;
import dev.arakiel.gemini.component.internal.world.ComponentPersistentState;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public abstract class MixinServerWorld extends MixinWorld {
   @Unique
   private static final String PERSISTENT_STATE_KEY = "gemini_world_components";

   @Shadow
   public abstract DimensionDataStorage getDataStorage();

   @Shadow
   public abstract List<ServerPlayer> players();

   @Inject(
      at = {@At("RETURN")},
      method = {"<init>*"}
   )
   private void constructor(CallbackInfo ci) {
      this.getDataStorage().computeIfAbsent(ComponentPersistentState.getType(this.components), "gemini_world_components");
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
      this.getComponentContainer().tickServerComponents();
   }

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      return List.copyOf(this.players());
   }

   public <C extends ComponentSync.AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(GeminiWorldComponents.PACKET_ID, Unit.INSTANCE, required, key.getId(), data);
   }
}
