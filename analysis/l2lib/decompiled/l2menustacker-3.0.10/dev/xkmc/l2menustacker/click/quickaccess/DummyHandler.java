package dev.xkmc.l2menustacker.click.quickaccess;

import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public record DummyHandler(Player player) implements ContainerLevelAccess {
   public <T> Optional<T> evaluate(BiFunction<Level, BlockPos, T> func) {
      func.apply(this.player.level(), this.player.getOnPos());
      return Optional.empty();
   }
}
