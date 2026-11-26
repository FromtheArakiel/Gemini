package dev.arakiel.gemini.event.enchant;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class GrindstoneEvent extends GeminiEvent {
    private final ItemStack top;
    private final ItemStack bottom;
    private int xp;

    protected GrindstoneEvent(ItemStack top, ItemStack bottom, int xp) {
        this.top = Objects.requireNonNull(top, "top");
        this.bottom = Objects.requireNonNull(bottom, "bottom");
        this.xp = xp;
    }

    public ItemStack getTopItem() {
        return this.top;
    }

    public ItemStack getBottomItem() {
        return this.bottom;
    }

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getExperience() {
        return this.xp;
    }

    public void setExperience(int experience) {
        this.xp = experience;
    }

    @Cancelable
    public static class OnPlaceItem extends GrindstoneEvent {
        private ItemStack output = ItemStack.EMPTY;

        public OnPlaceItem(ItemStack top, ItemStack bottom, int xp) {
            super(top, bottom, xp);
        }

        public ItemStack getOutput() {
            return this.output;
        }

        public void setOutput(ItemStack output) {
            this.output = Objects.requireNonNull(output, "output");
        }
    }

    @Cancelable
    public static class OnTakeItem extends GrindstoneEvent {
        private final ContainerLevelAccess access;
        private final @Nullable Player player;
        private ItemStack newTopItem = ItemStack.EMPTY;
        private ItemStack newBottomItem = ItemStack.EMPTY;

        public OnTakeItem(
                ContainerLevelAccess access,
                @Nullable Player player,
                ItemStack top,
                ItemStack bottom,
                int xp
        ) {
            super(top, bottom, xp);
            this.access = Objects.requireNonNull(access, "access");
            this.player = player;
        }

        public ItemStack getNewTopItem() {
            return this.newTopItem;
        }

        public void setNewTopItem(ItemStack newTopItem) {
            this.newTopItem = Objects.requireNonNull(newTopItem, "newTopItem");
        }

        public ItemStack getNewBottomItem() {
            return this.newBottomItem;
        }

        public void setNewBottomItem(ItemStack newBottomItem) {
            this.newBottomItem = Objects.requireNonNull(newBottomItem, "newBottomItem");
        }

        public ContainerLevelAccess getContainerAccess() {
            return this.access;
        }

        public @Nullable Player getPlayer() {
            return this.player;
        }
    }
}
