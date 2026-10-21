package dev.xkmc.l2core.events;

import com.mojang.datafixers.util.Either;
import dev.xkmc.l2core.init.L2CoreConfig;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.init.reg.ench.CustomDescEnchantment;
import dev.xkmc.l2core.init.reg.ench.EnchColor;
import dev.xkmc.l2core.init.reg.ench.LegacyEnchantment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2core", bus = Bus.GAME)
public class ClientEventHandler {
   @SubscribeEvent(priority = EventPriority.LOW)
   public static void modifyItemTooltip(ItemTooltipEvent event) {
      if (L2LibReg.ENCH.reg().size() != 0) {
         ClientEventHandler.EnchDesc config = (ClientEventHandler.EnchDesc)L2CoreConfig.CLIENT.addEnchantmentDescription.get();
         boolean skip = config == ClientEventHandler.EnchDesc.DISABLE || config == ClientEventHandler.EnchDesc.SHIFT_ONLY && !Screen.hasShiftDown();
         List<Component> list = event.getToolTip();
         int n = list.size();
         ItemStack stack = event.getItemStack();
         if (stack.isEnchanted() || stack.is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments map = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            String prefix = "enchantment.";
            String suffix = ".desc";
            boolean alt = Screen.hasAltDown();
            boolean flag = false;
            boolean book = event.getItemStack().is(Items.ENCHANTED_BOOK);
            Provider registries = event.getContext().registries();
            if (registries != null) {
               Optional<RegistryLookup<Enchantment>> reg = registries.lookup(Registries.ENCHANTMENT);
               if (!reg.isEmpty()) {
                  List<Either<Component, List<Component>>> compound = new ArrayList<>();

                  for (Component e : list) {
                     compound.add(Either.left(e));
                  }

                  for (int i = 0; i < n; i++) {
                     Component comp = list.get(i);
                     Component lit;
                     if (comp.getContents() instanceof PlainTextContents txt && !comp.getSiblings().isEmpty()) {
                        comp = (Component)comp.getSiblings().getFirst();
                        lit = Component.literal(txt.text());
                     } else {
                        lit = Component.empty();
                     }

                     if (comp.getContents() instanceof TranslatableContents tr && tr.getKey().startsWith(prefix)) {
                        if (tr.getKey().endsWith(suffix)) {
                           compound.set(i, Either.right(List.of()));
                           flag = true;
                        } else {
                           String id = tr.getKey().substring(prefix.length());
                           ResourceLocation rl = ResourceLocation.tryBySeparator(id, '.');
                           if (rl != null) {
                              Optional<Reference<Enchantment>> ench = reg.get().get(ResourceKey.create(Registries.ENCHANTMENT, rl));
                              if (!ench.isEmpty()) {
                                 EnchColor color = LegacyEnchantment.firstOf((Holder<Enchantment>)ench.get(), L2LibReg.COLOR);
                                 if (color != null) {
                                    comp = comp.copy().withStyle(color.base());
                                    compound.set(i, Either.left(comp));
                                    flag = true;
                                 } else {
                                    color = EnchColor.DEFAULT;
                                 }

                                 Optional<CustomDescEnchantment> legacy = LegacyEnchantment.findFirst(
                                    (Holder<Enchantment>)ench.get(), CustomDescEnchantment.class
                                 );
                                 if (legacy.isPresent()) {
                                    comp = legacy.get().title(stack, list.get(i), alt, book, color);
                                    compound.set(i, Either.left(comp));
                                    flag = true;
                                 }

                                 if (!skip) {
                                    if (legacy.isEmpty()) {
                                       if (I18n.exists(tr.getKey() + ".desc")) {
                                          compound.set(i, Either.right(List.of(comp, Component.translatable(tr.getKey() + ".desc").withStyle(color.desc()))));
                                          flag = true;
                                       }
                                    } else {
                                       int lv = map.getLevel((Holder)ench.get());
                                       List<Component> es = legacy.get().descFull(stack, lv, tr.getKey() + ".desc", alt, book, color);
                                       compound.set(i, Either.right(Stream.concat(Stream.of(comp), es.stream().map(e -> lit.copy().append(e))).toList()));
                                       flag = true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (flag) {
                     list.clear();
                     list.addAll(compound.stream().flatMap(e -> (Stream<? extends Component>)e.map(Stream::of, Collection::stream)).toList());
                  }
               }
            }
         }
      }
   }

   public enum EnchDesc {
      DISABLE,
      SHIFT_ONLY,
      ALWAYS;
   }
}
