package dev.xkmc.l2core.base.worldgen;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.serial.configval.DoubleConfigValue;
import java.util.function.DoubleSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ConfigRarityFilter extends PlacementFilter {
   public static final MapCodec<ConfigRarityFilter> CODEC = DoubleConfigValue.CODEC.fieldOf("chance").xmap(ConfigRarityFilter::new, e -> e.chance);
   private final DoubleSupplier chance;

   private ConfigRarityFilter(DoubleSupplier chance) {
      this.chance = chance;
   }

   public static ConfigRarityFilter of(DoubleSupplier chance) {
      return new ConfigRarityFilter(chance);
   }

   protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
      return random.nextFloat() < this.chance.getAsDouble();
   }

   public PlacementModifierType<?> type() {
      return L2LibReg.PM_RARITY.get();
   }
}
