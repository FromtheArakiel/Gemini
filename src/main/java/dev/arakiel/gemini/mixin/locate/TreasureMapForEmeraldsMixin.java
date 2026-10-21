package dev.arakiel.gemini.mixin.locate;

import dev.arakiel.gemini.locate.GeminiLocate;
import dev.arakiel.gemini.locate.internal.LocateMaps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$TreasureMapForEmeralds")
public abstract class TreasureMapForEmeraldsMixin {
    @Shadow
    @Final
    private int emeraldCost;

    @Shadow
    @Final
    private TagKey<Structure> destination;

    @Shadow
    @Final
    private String displayName;

    @Shadow
    @Final
    private Holder<MapDecorationType> destinationType;

    @Shadow
    @Final
    private int maxUses;

    @Shadow
    @Final
    private int villagerXp;

    @Inject(method = "getOffer", at = @At("HEAD"), cancellable = true)
    private void gemini$createMapOfferAsync(
            Entity trader,
            RandomSource random,
            CallbackInfoReturnable<MerchantOffer> callback
    ) {
        if (!GeminiLocate.config().villagerMaps || !(trader.level() instanceof ServerLevel level)) {
            return;
        }

        BlockPos origin = trader.blockPosition();
        ItemStack pending = LocateMaps.createPending(level, (byte) 2);
        MerchantOffer offer = new MerchantOffer(
                new ItemCost(Items.EMERALD, emeraldCost),
                Optional.of(new ItemCost(Items.COMPASS)),
                pending,
                maxUses,
                villagerXp,
                0.2F
        );
        GeminiLocate.findStructure(level, destination, origin, 100, true)
                .withTimeout(GeminiLocate.config().mapTimeoutSeconds, TimeUnit.SECONDS)
                .handleOnServerThread((found, throwable) -> handleResult(
                        trader,
                        level,
                        pending,
                        offer,
                        found,
                        throwable,
                        origin
                ));
        callback.setReturnValue(offer);
    }

    private void handleResult(
            Entity trader,
            ServerLevel level,
            ItemStack pending,
            MerchantOffer offer,
            @Nullable BlockPos found,
            @Nullable Throwable throwable,
            BlockPos origin
    ) {
        if (!GeminiLocate.isLevelActive(level) || !trader.isAlive()) {
            return;
        }
        Merchant merchant = trader instanceof Merchant value ? value : null;
        if (throwable == null && found != null) {
            LocateMaps.complete(
                    pending,
                    level,
                    found,
                    2,
                    destinationType,
                    Component.translatable(displayName),
                    origin
            );
            if (merchant != null) {
                merchant.notifyTradeUpdated(pending);
            }
        } else if (merchant != null) {
            LocateMaps.invalidateMerchantOffer(merchant, offer, pending, level, origin);
        } else {
            LocateMaps.invalidate(pending, level, origin);
        }
    }
}
