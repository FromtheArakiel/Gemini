package dev.arakiel.gemini.mixin.locate;

import com.llamalad7.mixinextras.sugar.Local;
import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.LocateTask;
import dev.arakiel.gemini.locate.internal.PendingEye;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.EnderEyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.TimeUnit;

@Mixin(EnderEyeItem.class)
public abstract class EnderEyeItemMixin {
    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;findNearestMapStructure(Lnet/minecraft/tags/TagKey;Lnet/minecraft/core/BlockPos;IZ)Lnet/minecraft/core/BlockPos;"
            )
    )
    private BlockPos gemini$deferStructureSearch(
            ServerLevel level,
            net.minecraft.tags.TagKey<net.minecraft.world.level.levelgen.structure.Structure> tag,
            BlockPos origin,
            int radius,
            boolean skipKnown
    ) {
        return GeminiLocate.config().eyeOfEnder
                ? BlockPos.ZERO
                : level.findNearestMapStructure(tag, origin, radius, skipKnown);
    }

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/EyeOfEnder;setItem(Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void gemini$startEyeLocate(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback,
            @Local EyeOfEnder eye,
            @Local ServerLevel serverLevel,
            @Local BlockPos origin
    ) {
        if (GeminiLocate.config().eyeOfEnder) {
            ((PendingEye) eye).gemini$setLocating(true);
            LocateTask<BlockPos> task = GeminiLocate.findStructure(
                            serverLevel,
                            StructureTags.EYE_OF_ENDER_LOCATED,
                            origin,
                            100,
                            false
                    )
                    .withTimeout(GeminiLocate.config().eyeTimeoutSeconds, TimeUnit.SECONDS);
            task.handleOnServerThread((found, throwable) -> handleResult(
                    serverLevel,
                    player,
                    eye,
                    task,
                    found,
                    throwable
            ));
        }
    }

    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/EyeOfEnder;signalTo(Lnet/minecraft/core/BlockPos;)V"
            )
    )
    private void gemini$deferSignal(EyeOfEnder eye, BlockPos pos) {
        if (!GeminiLocate.config().eyeOfEnder) {
            eye.signalTo(pos);
        }
    }

    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancements/critereon/UsedEnderEyeTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;)V"
            )
    )
    private void gemini$deferAdvancement(
            net.minecraft.advancements.critereon.UsedEnderEyeTrigger trigger,
            ServerPlayer player,
            BlockPos pos
    ) {
        if (!GeminiLocate.config().eyeOfEnder) {
            trigger.trigger(player, pos);
        }
    }

    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;)V"
            )
    )
    private void gemini$deferStat(Player player, Stat<?> stat) {
        if (!GeminiLocate.config().eyeOfEnder) {
            player.awardStat(stat);
        }
    }

    private static void handleResult(
            ServerLevel level,
            Player player,
            EyeOfEnder eye,
            LocateTask<BlockPos> task,
            BlockPos found,
            Throwable throwable
    ) {
        if (!GeminiLocate.isLevelActive(level)) {
            return;
        }
        PendingEye pending = (PendingEye) eye;
        pending.gemini$setLocating(false);
        if (!eye.isAlive() || eye.isRemoved()) {
            return;
        }
        if (throwable != null || found == null) {
            if (throwable != null) {
                task.cancel();
            }
            refund(level, player, eye);
            return;
        }

        eye.signalTo(found);
        if (!player.isRemoved() && player.level() == level) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.USED_ENDER_EYE.trigger(serverPlayer, found);
            }
            player.awardStat(Stats.ITEM_USED.get(Items.ENDER_EYE));
        }
    }

    private static void refund(ServerLevel level, Player player, EyeOfEnder eye) {
        if (!player.isCreative()) {
            level.addFreshEntity(new ItemEntity(
                    level,
                    eye.getX(),
                    eye.getY(),
                    eye.getZ(),
                    new ItemStack(Items.ENDER_EYE)
            ));
        }
        eye.discard();
    }
}
