package dev.xkmc.l2core.serial.configval;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.xkmc.l2core.util.ConfigInit;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public record DoubleConfigValue(String path, List<String> line) implements DoubleSupplier {
   public static Codec<DoubleSupplier> CODEC = Codec.either(Codec.DOUBLE, Codec.STRING)
      .xmap(
         e -> (DoubleSupplier)e.map(x -> () -> x, DoubleConfigValue::of),
         e -> e instanceof DoubleConfigValue val ? Either.right(val.toData()) : Either.left(e.getAsDouble())
      );

   public static <T extends ConfigInit> DoubleConfigValue of(T file, Function<T, ConfigValue<Double>> config) {
      return new DoubleConfigValue(file.getPath(), config.apply(file).getPath());
   }

   public static DoubleConfigValue of(String data) {
      int last = data.lastIndexOf(47);
      String[] line = data.substring(last + 1).split("\\.");
      return new DoubleConfigValue(data.substring(0, last), List.of(line));
   }

   @Override
   public double getAsDouble() {
      return AbstractConfigParser.parse(this.path, this.line).map(e -> e instanceof Number val ? val.doubleValue() : 0.0).orElse(0.0);
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
