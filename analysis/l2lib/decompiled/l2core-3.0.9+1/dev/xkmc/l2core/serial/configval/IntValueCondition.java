package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import java.util.ArrayList;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;

public record IntValueCondition(String path, ArrayList<String> line, int low, int high) implements ICondition {
   public static <T extends ConfigInit> IntValueCondition of(T file, Function<T, ConfigValue<Double>> config, int low, int high) {
      return new IntValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), low, high);
   }

   public boolean test(IContext context) {
      return AbstractConfigParser.parse(this.path, this.line).map(e -> e instanceof Integer val && this.low <= val && val <= this.high).orElse(false);
   }

   public MapCodec<IntValueCondition> codec() {
      return L2LibReg.CONDITION_INT.get();
   }
}
