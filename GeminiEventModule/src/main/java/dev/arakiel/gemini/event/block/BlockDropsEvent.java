package dev.arakiel.gemini.event.block;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.GeminiEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Cancelable
public class BlockDropsEvent extends GeminiEvent {
    private final ServerLevel level;
    private final BlockPos pos;
    private final BlockState state;
    private final @Nullable ServerPlayer player;
    private final ItemStack tool;
    private final List<ItemStack> drops;
    private int experience;

    public BlockDropsEvent(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            @Nullable ServerPlayer player,
            ItemStack tool,
            List<ItemStack> drops,
            int experience
    ) {
        this.level = Objects.requireNonNull(level, "level");
        this.pos = Objects.requireNonNull(pos, "pos");
        this.state = Objects.requireNonNull(state, "state");
        this.player = player;
        this.tool = Objects.requireNonNull(tool, "tool");
        this.drops = Objects.requireNonNull(drops, "drops");
        this.experience = Math.max(0, experience);
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getState() {
        return this.state;
    }

    public @Nullable ServerPlayer getPlayer() {
        return this.player;
    }

    public ItemStack getTool() {
        return this.tool;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }

    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int experience) {
        this.experience = Math.max(0, experience);
    }
}
