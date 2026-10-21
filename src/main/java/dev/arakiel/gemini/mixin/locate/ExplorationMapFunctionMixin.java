package dev.arakiel.gemini.mixin.locate;

import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.internal.LocateMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.TimeUnit;

@Mixin(ExplorationMapFunction.class)
public abstract class ExplorationMapFunctionMixin {
    @Shadow
    @Final
    private TagKey<Structure> destination;

    @Shadow
    @Final
    private Holder<MapDecorationType> mapDecoration;

    @Shadow
    @Final
    private byte zoom;

    @Shadow
    @Final
    private int searchRadius;

    @Shadow
    @Final
    private boolean skipKnownStructures;

    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void gemini$locateMapAsync(
            ItemStack stack,
            LootContext context,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        if (!GeminiLocate.config().explorationMaps || !stack.is(Items.MAP)) {
            return;
        }
        Vec3 originVector = context.getParamOrNull(LootContextParams.ORIGIN);
        if (originVector == null) {
            return;
        }

        ServerLevel level = context.getLevel();
        BlockPos origin = BlockPos.containing(originVector);
        ItemStack pending = LocateMaps.createPending(level, zoom);
        GeminiLocate.findStructure(level, destination, origin, searchRadius, skipKnownStructures)
                .withTimeout(GeminiLocate.config().mapTimeoutSeconds, TimeUnit.SECONDS)
                .handleOnServerThread((found, throwable) -> {
                    if (!GeminiLocate.isLevelActive(level)) {
                        return;
                    }
                    if (throwable == null && found != null) {
                        LocateMaps.complete(pending, level, found, zoom, mapDecoration, null, origin);
                    } else {
                        LocateMaps.invalidate(pending, level, origin);
                    }
                });
        callback.setReturnValue(pending);
    }
}
