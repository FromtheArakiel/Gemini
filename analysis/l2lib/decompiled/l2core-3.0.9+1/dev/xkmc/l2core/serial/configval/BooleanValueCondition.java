package dev.xkmc.l2core.serial.configval;

import com.mojang.serialization.MapCodec;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.ConfigInit;
import java.util.ArrayList;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;

public record BooleanValueCondition(String path, ArrayList<String> line, boolean expected) implements ICondition {
   public static <T extends ConfigInit> BooleanValueCondition of(T file, Function<T, ConfigValue<Boolean>> config, boolean value) {
      return new BooleanValueCondition(file.getPath(), new ArrayList<>(config.apply(file).getPath()), value);
   }

   public boolean test(IContext context) {
      return AbstractConfigParser.parse(this.path, this.line).map(e -> e instanceof Boolean bool && bool == this.expected).orElse(false);
   }

   public MapCodec<BooleanValueCondition> codec() {
      return L2LibReg.CONDITION_BOOL.get();
   }
}
