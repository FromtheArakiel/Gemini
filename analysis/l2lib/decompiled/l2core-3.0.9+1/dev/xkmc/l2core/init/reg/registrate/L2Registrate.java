package dev.xkmc.l2core.init.reg.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.NoConfigBuilder;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.reg.simple.Val;
import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2serial.serialization.custom_handler.CodecHandler;
import dev.xkmc.l2serial.serialization.custom_handler.Handlers;
import dev.xkmc.l2serial.util.ModContainerHack;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.client.particle.ParticleProvider.Sprite;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

public class L2Registrate extends AbstractRegistrate<L2Registrate> {
   public final NonNullSupplier<Boolean> doDataGen = NonNullSupplier.lazy(DatagenModLoader::isRunningDataGen);
   private boolean initConfig = false;

   public L2Registrate(String modid) {
      super(modid);
      ModContainer mod = ModContainerHack.getMod(modid);
      IEventBus bus = mod.getEventBus();
      if (bus != null) {
         this.registerEventListeners(bus);
      } else {
         L2Core.LOGGER.error("Failed to register mod {}", modid);
      }

      this.addRawLang(modid + ".title", mod.getModInfo().getDisplayName());
   }

   public ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath(this.getModid(), id);
   }

   public <T extends ConfigInit> T registerClient(Function<ConfigInit.Builder, T> factory) {
      return ConfigInit.register(this, Type.CLIENT, factory);
   }

   public <T extends ConfigInit> T registerUnsynced(Function<ConfigInit.Builder, T> factory) {
      return ConfigInit.register(this, Type.COMMON, factory);
   }

   public <T extends ConfigInit> T registerSynced(Function<ConfigInit.Builder, T> factory) {
      return ConfigInit.register(this, Type.SERVER, factory);
   }

   public void initConfigTitle(ModContainer mod) {
      if (!this.initConfig) {
         this.initConfig = true;
         this.addRawLang(this.getModid() + ".configuration.title", mod.getModInfo().getDisplayName() + " Configuration");
      }
   }

   public <T, P extends T> L2Registrate.GenericBuilder<T, P> generic(L2Registrate.RegistryInstance<T> cls, String id, NonNullSupplier<P> sup) {
      return (L2Registrate.GenericBuilder<T, P>)this.entry(id, cb -> new L2Registrate.GenericBuilder(this, id, cb, cls.key(), sup));
   }

   public <T extends MobEffect> NoConfigBuilder<MobEffect, T, L2Registrate> effect(String name, NonNullSupplier<T> sup, String desc) {
      this.addRawLang("effect." + this.getModid() + "." + name + ".description", desc);
      this.addRawLang("effect." + this.getModid() + "." + name + ".desc", desc);
      return (NoConfigBuilder<MobEffect, T, L2Registrate>)this.entry(name, cb -> new NoConfigBuilder(this, this, name, cb, Registries.MOB_EFFECT, sup));
   }

   public <T extends Potion> SimpleEntry<Potion> potion(String name, NonNullSupplier<T> sup) {
      return this.potion(name, RegistrateLangProvider.toEnglishName(name), sup);
   }

   public <T extends Potion> SimpleEntry<Potion> potion(String name, String desc, NonNullSupplier<T> sup) {
      RegistryEntry<Potion, T> ans = ((NoConfigBuilder)this.entry(name, cb -> new NoConfigBuilder(this, this, name, cb, Registries.POTION, sup))).register();
      if ((Boolean)this.doDataGen.get()) {
         for (Item item : List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW)) {
            String pref = item.getDescriptionId();
            String[] prefs = pref.split("\\.");
            String str = item.getDescriptionId() + ".effect." + name;
            String pref_name = RegistrateLangProvider.toEnglishName(prefs[prefs.length - 1]);
            if (item == Items.TIPPED_ARROW) {
               pref_name = "Arrow";
            }

            this.addRawLang(str, pref_name + " of " + desc);
         }
      }

      return new SimpleEntry<>(ans);
   }

   public <T extends ParticleOptions, R extends ParticleType<T>> Val<R> particle(
      String name, NonNullSupplier<R> sup, NonNullSupplier<? extends L2Registrate.ParticleSupplier<T>> pvd
   ) {
      RegistryEntry<ParticleType<?>, R> ans = ((NoConfigBuilder)this.entry(name, cb -> new NoConfigBuilder(this, this, name, cb, Registries.PARTICLE_TYPE, sup)))
         .register();
      RegistrateDistExecutor.unsafeRunWhenOn(
         Dist.CLIENT,
         () -> () -> OneTimeEventReceiver.addModListener(
            this,
            RegisterParticleProvidersEvent.class,
            event -> ((L2Registrate.ParticleSupplier)pvd.get()).register().accept((T)event, (ParticleType)ans.get())
         )
      );
      return new Val.Registrate<>(ans);
   }

   public <E> L2Registrate.RegistryInstance<E> newRegistry(String id) {
      ResourceKey<Registry<E>> key = ResourceKey.createRegistryKey(this.loc(id));
      RegistryBuilder<E> ans = new RegistryBuilder(key);
      Registry<E> reg = ans.create();
      OneTimeEventReceiver.addModListener(this, NewRegistryEvent.class, e -> e.register(reg));
      return new L2Registrate.RegistryInstance<>(reg, key);
   }

   public <E> L2Registrate.RegistryInstance<E> newRegistry(String id, Class<?> cls, Consumer<RegistryBuilder<E>> cons) {
      ResourceKey<Registry<E>> key = ResourceKey.createRegistryKey(this.loc(id));
      RegistryBuilder<E> ans = new RegistryBuilder(key);
      cons.accept(ans);
      Registry<E> reg = ans.create();
      new CodecHandler((Class)Wrappers.cast(cls), reg.byNameCodec(), ByteBufCodecs.fromCodecWithRegistries(reg.byNameCodec()));
      Handlers.registerReg((Class)Wrappers.cast(cls), key);
      OneTimeEventReceiver.addModListener(this, NewRegistryEvent.class, e -> e.register(reg));
      return new L2Registrate.RegistryInstance<>(reg, key);
   }

   public <E> L2Registrate.RegistryInstance<E> newRegistry(String id, Class<?> cls) {
      return this.newRegistry(id, cls, e -> {});
   }

   public synchronized SimpleEntry<CreativeModeTab> buildModCreativeTab(String name, String def, Consumer<Builder> config) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(this.getModid(), name);
      this.defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
      return this.buildCreativeTabImpl(name, this.addLang("itemGroup", id, def), config);
   }

   public synchronized SimpleEntry<CreativeModeTab> buildL2CreativeTab(String name, String def, Consumer<Builder> config) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("l2core", name);
      this.defaultCreativeTab(ResourceKey.create(Registries.CREATIVE_MODE_TAB, id));
      L2Registrate.TabSorter sorter = new L2Registrate.TabSorter(this.getModid() + ":" + name, id);
      return L2Core.REGISTRATE.buildCreativeTabImpl(name, this.addLang("itemGroup", id, def), b -> {
         config.accept(b);
         sorter.sort(b);
      });
   }

   private synchronized SimpleEntry<CreativeModeTab> buildCreativeTabImpl(String name, Component comp, Consumer<Builder> config) {
      return new SimpleEntry<>(this.generic((L2Registrate)this.self(), name, Registries.CREATIVE_MODE_TAB, () -> {
         Builder builder = CreativeModeTab.builder().title(comp).withTabsBefore(new ResourceKey[]{CreativeModeTabs.SPAWN_EGGS});
         config.accept(builder);
         return builder.build();
      }).register());
   }

   public static class GenericBuilder<T, P extends T> extends AbstractBuilder<T, P, L2Registrate, L2Registrate.GenericBuilder<T, P>> {
      private final NonNullSupplier<P> sup;

      GenericBuilder(L2Registrate parent, String name, BuilderCallback callback, ResourceKey<Registry<T>> registryType, NonNullSupplier<P> sup) {
         super(parent, parent, name, callback, registryType);
         this.sup = sup;
      }

      @NonnullType
      @NotNull
      protected P createEntry() {
         return (P)this.sup.get();
      }

      public L2Registrate.GenericBuilder<T, P> defaultLang() {
         ResourceLocation reg = this.getRegistryKey().location();
         String id = reg.getPath() + "." + this.getOwner().getModid() + "." + this.getName();
         return (L2Registrate.GenericBuilder<T, P>)this.lang(e -> id, RegistrateLangProvider.toEnglishName(this.getName()));
      }
   }

   public interface ParticleSupplier<T extends ParticleOptions> {
      static <T extends ParticleOptions> L2Registrate.ParticleSupplier<T> provider(NonNullSupplier<ParticleProvider<T>> pvd) {
         return () -> (event, type) -> event.registerSpecial(type, (ParticleProvider)pvd.get());
      }

      static <T extends ParticleOptions> L2Registrate.ParticleSupplier<T> sprite(NonNullSupplier<Sprite<T>> pvd) {
         return () -> (event, type) -> event.registerSprite(type, (Sprite)pvd.get());
      }

      static <T extends ParticleOptions> L2Registrate.ParticleSupplier<T> spriteSet(NonNullSupplier<SpriteParticleRegistration<T>> pvd) {
         return () -> (event, type) -> event.registerSpriteSet(type, (SpriteParticleRegistration)pvd.get());
      }

      BiConsumer<RegisterParticleProvidersEvent, ParticleType<T>> register();
   }

   public record RegistryInstance<E>(Registry<E> reg, ResourceKey<Registry<E>> key) implements Supplier<Registry<E>> {
      public Registry<E> get() {
         return this.reg;
      }
   }

   private static class TabSorter {
      private static final TreeMap<String, L2Registrate.TabSorter> MAP = new TreeMap<>();
      private static final HashSet<ResourceLocation> SET = new HashSet<>();
      private final ResourceLocation id;

      private TabSorter(String str, ResourceLocation id) {
         MAP.put(str, this);
         SET.add(id);
         this.id = id;
      }

      public void sort(Builder b) {
         ArrayList<L2Registrate.TabSorter> list = new ArrayList<>(MAP.values());
         boolean after = false;
         ResourceLocation before = null;

         for (L2Registrate.TabSorter e : list) {
            if (e == this) {
               after = true;
               if (before != null) {
                  b.withTabsBefore(new ResourceLocation[]{before});
               }
            } else {
               if (after) {
                  b.withTabsAfter(new ResourceLocation[]{e.id});
                  return;
               }

               before = e.id;
            }
         }
      }

      private static boolean known(ResourceLocation id) {
         return id.getNamespace().equals("minecraft") ? true : SET.contains(id);
      }

      private static boolean known(CreativeModeTab tab) {
         for (ResourceLocation other : tab.tabsAfter) {
            if (known(other)) {
               return true;
            }
         }

         return false;
      }
   }
}
