package dev.xkmc.l2core.init.reg.varitem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.logging.log4j.Level;

public class VarItemInit<T extends Item> {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
   private static final ConcurrentMap<ResourceLocation, VarItemInit<?>> VAR_ITEM_TYPE = new ConcurrentHashMap<>();
   private static final String PATH = "varitem_config";
   private final L2Registrate reg;
   private final ResourceLocation id;
   private final Function<ResourceLocation, T> func;
   private final VarBuilder<T> builder;
   private final Map<String, VarEntry<T>> defaults = new LinkedHashMap<>();
   private final Set<String> registered = new LinkedHashSet<>();
   private final Map<String, ItemEntry<T>> results = new ConcurrentHashMap<>();

   public static <T extends Item> VarItemInit<T> setup(L2Registrate reg, ResourceLocation id, Function<ResourceLocation, T> func, VarBuilder<T> builder) {
      VarItemInit<T> ans = new VarItemInit<>(reg, id, func, builder);
      VAR_ITEM_TYPE.put(id, ans);
      return ans;
   }

   private VarItemInit(L2Registrate reg, ResourceLocation id, Function<ResourceLocation, T> func, VarBuilder<T> builder) {
      this.reg = reg;
      this.id = id;
      this.func = func;
      this.builder = builder;
      reg.getModEventBus().addListener(EventPriority.HIGH, RegisterEvent.class, this::init);
   }

   public synchronized void add(List<String> defaults) {
      defaults.forEach(e -> this.defaults.put(e, new VarItemInit.SimpleVarEntry<>(e)));
   }

   public synchronized VarHolder<T> add(VarHolder<T> e) {
      this.defaults.put(e.id(), e);
      return e;
   }

   @Nullable
   public ItemEntry<T> get(String str) {
      return this.results.get(str);
   }

   private synchronized void init(RegisterEvent event) {
      if (event.getRegistry() == BuiltInRegistries.ITEM) {
         this.load();

         for (String e : this.registered) {
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(this.reg.getModid(), e);
            if (BuiltInRegistries.ITEM.containsKey(rl)) {
               L2Core.LOGGER.error("Item ID {} is already used. Registration of varitem type {} skips this ID.", rl, this.id);
            } else {
               L2Core.LOGGER.debug("Registering {} for varitem type {}", rl, this.id);
               VarEntry<T> def = this.defaults.get(e);
               ItemBuilder<T, L2Registrate> b = this.builder.build(rl, this.reg.item(e, p -> this.func.apply(rl)));
               if (def != null) {
                  b = def.builder().build(rl, b);
               }

               ItemEntry<T> item = b.register();
               this.results.put(e, item);
               if (def != null) {
                  def.callback(item);
               }
            }
         }
      }
   }

   private void load() {
      String path = "varitem_config/" + this.id.getNamespace() + "-" + this.id.getPath() + ".json";
      File file = FMLPaths.CONFIGDIR.get().resolve(path).toFile();
      this.registered.clear();

      try {
         File parent = file.getParentFile();
         if (parent.exists()) {
            if (!parent.isDirectory() && parent.delete()) {
               parent.mkdirs();
            }
         } else {
            parent.mkdirs();
         }

         if (!file.exists()) {
            file.createNewFile();
         }

         FileReader reader = new FileReader(file);
         JsonElement json = JsonParser.parseReader(reader);
         reader.close();
         Pair<Boolean, List<String>> result = this.parseFile(json);
         List<String> ans = (List<String>)result.getSecond();
         if ((Boolean)result.getFirst()) {
            if (ans.isEmpty()) {
               ans = new ArrayList<>(this.defaults.keySet());
            }

            FileWriter writer = new FileWriter(file);
            JsonArray out = new JsonArray();

            for (String e : ans) {
               out.add(e);
            }

            writer.write(GSON.toJson(out));
            writer.close();
         }

         this.registered.addAll(ans);
      } catch (Exception e) {
         L2Core.LOGGER.error("Failed to parse config file for varitem type {}. Use defaults instead", this.id);
         L2Core.LOGGER.throwing(Level.ERROR, e);
         this.registered.addAll(this.defaults.keySet());
      }
   }

   private Pair<Boolean, List<String>> parseFile(JsonElement elem) {
      if (!elem.isJsonArray()) {
         return Pair.of(true, List.of());
      }

      Set<String> ans = new LinkedHashSet<>(this.defaults.keySet());
      Set<String> checker = new HashSet<>();
      boolean err = false;

      for (JsonElement e : elem.getAsJsonArray()) {
         String rl = e.getAsString();
         if (ResourceLocation.isValidPath(rl)) {
            ans.add(rl);
            checker.add(rl);
         } else {
            L2Core.LOGGER.error("Item ID {} for varitem type {} is invalid. Skipped", rl, this.id);
            err = true;
         }
      }

      if (checker.size() != ans.size()) {
         L2Core.LOGGER.error("Found missing default items for varitem type {}. Filling", this.id);
         err = true;
      }

      return Pair.of(err, new ArrayList<>(ans));
   }

   private record SimpleVarEntry<T extends Item>(String id) implements VarEntry<T> {
      @Override
      public void callback(ItemEntry<T> item) {
      }

      @Override
      public VarBuilder<T> builder() {
         return VarBuilder.nop();
      }
   }
}
