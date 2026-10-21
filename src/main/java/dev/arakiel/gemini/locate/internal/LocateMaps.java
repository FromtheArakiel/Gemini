package dev.arakiel.gemini.locate.internal;

import dev.arakiel.gemini.locate.GeminiLocate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class LocateMaps {
    private static final String PENDING = "gemini$locate_pending";
    private static final String PENDING_ID = "gemini$locate_pending_id";

    private LocateMaps() {
    }

    public static ItemStack createPending(ServerLevel level, byte scale) {
        ItemStack stack = MapItem.create(level, 0, 0, scale, true, true);
        stack.set(DataComponents.ITEM_NAME, Component.translatable("menu.working"));
        markPending(stack, UUID.randomUUID());
        return stack;
    }

    public static boolean isPending(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(PENDING);
    }

    public static void complete(
            ItemStack pending,
            ServerLevel level,
            BlockPos target,
            int scale,
            Holder<MapDecorationType> decoration,
            @Nullable Component name,
            @Nullable BlockPos origin
    ) {
        UUID id = pendingId(pending);
        ItemStack completed = MapItem.create(
                level,
                target.getX(),
                target.getZ(),
                (byte) scale,
                true,
                true
        );
        MapItem.renderBiomePreviewMap(level, completed);
        MapItemSavedData.addTargetDecoration(completed, target, "+", decoration);
        transferMapComponents(completed, pending);
        if (name != null) {
            pending.set(DataComponents.ITEM_NAME, name);
        }
        replaceContainers(level, id, pending, origin);
        clearPending(pending);
    }

    public static void invalidate(ItemStack pending, ServerLevel level, @Nullable BlockPos origin) {
        UUID id = pendingId(pending);
        ItemStack replacement = new ItemStack(Items.MAP);
        if (replaceContainers(level, id, replacement, origin)) {
            pending.setCount(0);
        } else {
            pending.setCount(0);
        }
    }

    public static UUID pendingId(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.contains(PENDING_ID)) {
            throw new IllegalArgumentException("Item stack is not a pending Gemini locate map");
        }
        return data.copyTag().getUUID(PENDING_ID);
    }

    public static void invalidateMerchantOffer(
            Merchant merchant,
            MerchantOffer offer,
            ItemStack pending,
            ServerLevel level,
            @Nullable BlockPos origin
    ) {
        if (GeminiLocate.config().removeInvalidMerchantOffers) {
            MerchantOffers offers = merchant.getOffers();
            offers.remove(offer);
            merchant.overrideOffers(offers);
        } else {
            offer.setToOutOfStock();
        }
        invalidate(pending, level, origin);
        merchant.notifyTradeUpdated(pending);
    }

    private static void transferMapComponents(ItemStack source, ItemStack target) {
        target.set(DataComponents.MAP_ID, source.get(DataComponents.MAP_ID));
        target.set(DataComponents.MAP_DECORATIONS, source.get(DataComponents.MAP_DECORATIONS));
        target.set(DataComponents.MAP_COLOR, source.get(DataComponents.MAP_COLOR));
    }

    private static void markPending(ItemStack stack, UUID id) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putBoolean(PENDING, true);
            tag.putUUID(PENDING_ID, id);
        });
    }

    private static void clearPending(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return;
        }
        var tag = data.copyTag();
        tag.remove(PENDING);
        tag.remove(PENDING_ID);
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    private static boolean replaceContainers(
            ServerLevel level,
            UUID id,
            ItemStack replacement,
            @Nullable BlockPos origin
    ) {
        boolean replaced = false;
        if (origin != null) {
            BlockEntity blockEntity = level.getBlockEntity(origin);
            if (blockEntity instanceof Container container) {
                replaced |= replace(container, id, replacement);
            }
        }
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            for (var slot : player.containerMenu.slots) {
                ItemStack stack = slot.getItem();
                if (isPending(stack) && pendingId(stack).equals(id)) {
                    slot.set(replacement.copy());
                    slot.setChanged();
                    replaced = true;
                }
            }
            player.containerMenu.broadcastChanges();
        }
        return replaced;
    }

    private static boolean replace(Container container, UUID id, ItemStack replacement) {
        boolean replaced = false;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (isPending(stack) && pendingId(stack).equals(id)) {
                container.setItem(slot, replacement.copy());
                replaced = true;
            }
        }
        if (replaced) {
            container.setChanged();
        }
        return replaced;
    }
}
