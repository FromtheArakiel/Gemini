package dev.arakiel.gemini.mixin.component.entity;

import dev.arakiel.gemini.component.internal.entity.SwitchablePlayerEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ServerPlayer.class})
public abstract class MixinServerPlayerEntity implements SwitchablePlayerEntity {
   private transient boolean switchingCharacter = false;

   @Override
   public void gemini$markAsSwitchingCharacter() {
      this.switchingCharacter = true;
   }

   @Override
   public boolean gemini$isSwitchingCharacter() {
      return this.switchingCharacter;
   }
}
