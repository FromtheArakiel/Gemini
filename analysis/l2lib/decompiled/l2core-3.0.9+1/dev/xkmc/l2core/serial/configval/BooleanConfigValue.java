package dev.xkmc.l2core.serial.configval;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.xkmc.l2core.util.ConfigInit;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public record BooleanConfigValue(String path, List<String> line) implements BooleanSupplier {
   public static Codec<BooleanSupplier> CODEC = Codec.either(Codec.BOOL, Codec.STRING)
      .xmap(
         e -> (BooleanSupplier)e.map(x -> () -> x, BooleanConfigValue::of),
         e -> e instanceof BooleanConfigValue val ? Either.right(val.toData()) : Either.left(e.getAsBoolean())
      );

   public static <T extends ConfigInit> BooleanConfigValue of(T file, Function<T, ConfigValue<Boolean>> config) {
      return new BooleanConfigValue(file.getPath(), config.apply(file).getPath());
   }

   public static BooleanConfigValue of(String data) {
      int last = data.lastIndexOf(47);
      String[] line = data.substring(last + 1).split("\\.");
      return new BooleanConfigValue(data.substring(0, last), List.of(line));
   }

   @Override
   public boolean getAsBoolean() {
      return AbstractConfigParser.parse(this.path, this.line).map(e -> e instanceof Boolean val ? val : false).orElse(false);
   }

   public String toData() {
      StringBuilder lines = new StringBuilder();

      for (String e : this.line) {
         if (!lines.isEmpty()) {
            lines.append(".");
         }

         lines.append(e);
      }

      return this.path + "/" + lines;
   }
}
