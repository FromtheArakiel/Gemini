package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.enchant.GrindstoneEvent;
import dev.arakiel.gemini.event.internal.GeminiGrindstoneAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
public abstract class GrindstoneTakeItemEventMixin {
    @Shadow(remap = false)
    @Final
    private ContainerLevelAccess val$access;

    @Shadow
    private int getExperienceAmount(Level level) {
        throw new AssertionError();
    }

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void gemini$grindstoneTake(Player player, ItemStack output, CallbackInfo callback) {
        GeminiGrindstoneAccess menu = (GeminiGrindstoneAccess) (Object) this.gemini$menu();
        this.val$access.execute((level, pos) -> {
            int experience = menu.gemini$getExperience();
            if (experience < 0) {
                experience = this.getExperienceAmount(level);
            }
            GrindstoneEvent.OnTakeItem event = new GrindstoneEvent.OnTakeItem(
                    this.val$access,
                    player,
                    menu.gemini$getRepairSlots().getItem(0),
                    menu.gemini$getRepairSlots().getItem(1),
                    experience
            );
            GeminiEvents.post(event);
            if (event.isCanceled()) {
                return;
            }

            if (level instanceof ServerLevel serverLevel) {
                ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), event.getXp());
            }
            level.levelEvent(1042, pos, 0);
            menu.gemini$getRepairSlots().setItem(0, event.getNewTopItem());
            menu.gemini$getRepairSlots().setItem(1, event.getNewBottomItem());
            menu.gemini$getRepairSlots().setChanged();
        });
        callback.cancel();
    }

    @Unique
    private GrindstoneMenu gemini$menu() {
        try {
            Field field = this.getClass().getDeclaredField("this$0");
            field.setAccessible(true);
            return (GrindstoneMenu) field.get(this);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to access the grindstone menu", exception);
        }
    }
}
