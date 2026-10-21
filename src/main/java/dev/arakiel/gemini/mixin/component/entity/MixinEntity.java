package dev.arakiel.gemini.mixin.component.entity;

import dev.arakiel.gemini.component.api.ComponentContainer;
import dev.arakiel.gemini.component.api.ComponentKey;
import dev.arakiel.gemini.component.api.ComponentProvider;
import dev.arakiel.gemini.component.api.ComponentSync;
import dev.arakiel.gemini.component.internal.network.ComponentUpdatePayload;
import dev.arakiel.gemini.component.internal.entity.GeminiEntityComponents;
import dev.arakiel.gemini.component.internal.entity.GeminiEntityInternals;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nonnull;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class MixinEntity implements ComponentProvider {
   @Unique
   private ComponentContainer components;
   @Shadow
   private Level level;

   @Shadow
   public abstract int getId();

   @Shadow
   public abstract RegistryAccess registryAccess();

   @Inject(
      method = {"<init>*"},
      at = {@At("RETURN")}
   )
   private void initDataTracker(CallbackInfo ci) {
      this.components = GeminiEntityInternals.createEntityComponentContainer((Entity)(Object)this);
   }

   @Inject(
      method = {"saveWithoutId"},
      at = {@At("RETURN")}
   )
   private void toTag(CompoundTag inputTag, CallbackInfoReturnable<CompoundTag> cir) {
      this.components.toTag((CompoundTag)cir.getReturnValue(), this.registryAccess());
   }

   @Inject(
      method = {"load"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
         shift = Shift.AFTER
      )}
   )
   private void fromTag(CompoundTag tag, CallbackInfo ci) {
      this.components.fromTag(tag, this.registryAccess());
   }

   @Nonnull
   @Override
   public ComponentContainer getComponentContainer() {
      return this.components;
   }

   @Override
   public Iterable<ServerPlayer> getRecipientsForComponentSync() {
      Entity holder = (Entity)(Object)this;
      if (!this.level.isClientSide) {
         Deque<ServerPlayer> watchers = new ArrayDeque<>(PlayerLookup.tracking(holder));
         if (holder instanceof ServerPlayer player && player.connection != null) {
            watchers.addFirst(player);
         }

         return watchers;
      } else {
         return List.of();
      }
   }

   public <C extends ComponentSync.AutoSyncedComponent> ComponentUpdatePayload<?> toComponentPacket(
      ComponentKey<? super C> key, boolean required, RegistryFriendlyByteBuf data
   ) {
      return new ComponentUpdatePayload<>(GeminiEntityComponents.PACKET_ID, this.getId(), required, key.getId(), data);
   }
}
