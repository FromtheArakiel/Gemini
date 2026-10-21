package com.tterrag.registrate.builders;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.RegistrateDistExecutor;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MenuBuilder<T extends AbstractContainerMenu, S extends Screen & MenuAccess<T>, P>
   extends AbstractBuilder<MenuType<?>, MenuType<T>, P, MenuBuilder<T, S, P>> {
   private final MenuBuilder.ForgeMenuFactory<T> factory;
   private final NonNullSupplier<MenuBuilder.ScreenFactory<T, S>> screenFactory;

   public MenuBuilder(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      MenuBuilder.MenuFactory<T> factory,
      NonNullSupplier<MenuBuilder.ScreenFactory<T, S>> screenFactory
   ) {
      this(owner, parent, name, callback, (type, windowId, inv, $) -> factory.create(type, windowId, inv), screenFactory);
   }

   public MenuBuilder(
      AbstractRegistrate<?> owner,
      P parent,
      String name,
      BuilderCallback callback,
      MenuBuilder.ForgeMenuFactory<T> factory,
      NonNullSupplier<MenuBuilder.ScreenFactory<T, S>> screenFactory
   ) {
      super(owner, parent, name, callback, Registries.MENU);
      this.factory = factory;
      this.screenFactory = screenFactory;
   }

   @NonnullType
   protected MenuType<T> createEntry() {
      MenuBuilder.ForgeMenuFactory<T> factory = this.factory;
      NonNullSupplier<MenuType<T>> supplier = this.asSupplier();
      MenuType<T> ret = IMenuTypeExtension.create((windowId, inv, buf) -> factory.create(supplier.get(), windowId, inv, buf));
      RegistrateDistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
         MenuBuilder.ScreenFactory<T, S> screenFactory = this.screenFactory.get();
         OneTimeEventReceiver.addModListener(this.getOwner(), RegisterMenuScreensEvent.class, event -> event.register(ret, screenFactory::create));
      });
      return ret;
   }

   @Override
   protected RegistryEntry<MenuType<?>, MenuType<T>> createEntryWrapper(DeferredHolder<MenuType<?>, MenuType<T>> delegate) {
      return new MenuEntry<>(this.getOwner(), delegate);
   }

   public MenuEntry<T> register() {
      return (MenuEntry<T>)super.register();
   }

   public interface ForgeMenuFactory<T extends AbstractContainerMenu> {
      T create(MenuType<T> var1, int var2, Inventory var3, @Nullable RegistryFriendlyByteBuf var4);
   }

   public interface MenuFactory<T extends AbstractContainerMenu> {
      T create(MenuType<T> var1, int var2, Inventory var3);
   }

   public interface ScreenFactory<M extends AbstractContainerMenu, T extends Screen & MenuAccess<M>> {
      T create(M var1, Inventory var2, Component var3);
   }
}
