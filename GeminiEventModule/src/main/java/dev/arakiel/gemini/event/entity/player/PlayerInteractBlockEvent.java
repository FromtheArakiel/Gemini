package dev.arakiel.gemini.event.entity.player;

import dev.arakiel.gemini.event.Cancelable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

@Cancelable
public class PlayerInteractBlockEvent extends PlayerEvent {
    private final InteractionHand hand;
    private final ItemStack stack;
    private final BlockHitResult hitResult;

    public PlayerInteractBlockEvent(Player player, InteractionHand hand, ItemStack stack, BlockHitResult hitResult) {
        super(player);
        this.hand = Objects.requireNonNull(hand, "hand");
        this.stack = Objects.requireNonNull(stack, "stack");
        this.hitResult = Objects.requireNonNull(hitResult, "hitResult");
    }

    public InteractionHand getHand() {
        return hand;
    }

    public ItemStack getStack() {
        return stack;
    }

    public BlockHitResult getHitResult() {
        return hitResult;
    }

    public BlockPos getPos() {
        return hitResult.getBlockPos();
    }

    public Direction getDirection() {
        return hitResult.getDirection();
    }
}
