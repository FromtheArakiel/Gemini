package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import java.util.ArrayList;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;

public record StringValueCondition(String path, ArrayList<String> line, String key) implements ICondition {
   public static <T extends ConfigInit> StringValueCondition of(T file, Function<T, ConfigValue<Double>> config, String key) {
      return new StringValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), key);
   }

   public boolean test(IContext context) {
      return AbstractConfigParser.parse(this.path, this.line).map(e -> e instanceof String val && val.equals(this.key)).orElse(false);
   }

   public MapCodec<StringValueCondition> codec() {
      return L2LibReg.CONDITION_STR.get();
   }
}
