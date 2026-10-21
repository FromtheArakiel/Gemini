package dev.xkmc.l2core.serial.advancements;

import java.util.List;
import net.minecraft.advancements.Advancement.Builder;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

public record ModLoadedAdv(String... modid) implements IAdvBuilder {
   @Override
   public void onBuild(String id, Builder builder, List<ICondition> conditions) {
      for (String e : this.modid) {
         conditions.add(new ModLoadedCondition(e));
      }
   }
}
