package dev.xkmc.l2core.serial.advancements;

import java.util.List;
import net.minecraft.advancements.Advancement.Builder;
import net.neoforged.neoforge.common.conditions.ICondition;

public interface IAdvBuilder {
   void onBuild(String var1, Builder var2, List<ICondition> var3);
}
