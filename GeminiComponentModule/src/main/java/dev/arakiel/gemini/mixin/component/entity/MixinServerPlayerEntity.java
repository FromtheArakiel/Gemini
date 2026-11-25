package dev.arakiel.gemini.mixin.component.entity;

import dev.arakiel.gemini.component.internal.entity.SwitchablePlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ServerPlayer.class})
public abstract class MixinServerPlayerEntity implements SwitchablePlayerEntity {
   @Unique
   private transient boolean gemini$switchingCharacter = false;

   @Override
   public void gemini$markAsSwitchingCharacter() {
      this.gemini$switchingCharacter = true;
   }

   @Override
   public boolean gemini$isSwitchingCharacter() {
      return this.gemini$switchingCharacter;
   }
}
