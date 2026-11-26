package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.living.MobSplitEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Slime.class)
public abstract class MobSplitEventMixin {
    @Redirect(
            method = "remove",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private void gemini$mobSplit(List<Mob> children, Consumer<Mob> consumer) {
        Slime parent = (Slime) (Object) this;
        MobSplitEvent event = new MobSplitEvent(parent, children);
        GeminiEvents.post(event);
        if (!event.isCanceled()) {
            event.getChildren().forEach(child -> parent.level().addFreshEntity(child));
        }
    }
}
