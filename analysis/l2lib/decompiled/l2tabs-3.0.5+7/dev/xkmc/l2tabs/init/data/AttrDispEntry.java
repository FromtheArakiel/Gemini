package dev.xkmc.l2tabs.init.data;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2tabs.init.L2Tabs;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public record AttrDispEntry(boolean usePercent, int order, double intrinsic) {
   public static List<Pair<Holder<Attribute>, AttrDispEntry>> get(LivingEntity le) {
      List<Pair<Holder<Attribute>, AttrDispEntry>> ans = L2Tabs.ATTRIBUTE_ENTRY
         .getAll(le.level().registryAccess())
         .filter(ex -> le.getAttribute((Holder)ex.getFirst()) != null)
         .sorted(Comparator.comparingInt(x -> ((AttrDispEntry)x.getSecond()).order))
         .toList();
      AttrDispEntry.AttributeDisplay disp = (AttrDispEntry.AttributeDisplay)L2TabsConfig.CLIENT.attributeSettings.get();
      if (disp != AttrDispEntry.AttributeDisplay.COMMON) {
         ans = new ArrayList<>(ans);
         ans.sort(Comparator.comparingInt(ex -> ((AttrDispEntry)ex.getSecond()).order));
         int latest = ans.isEmpty() ? 0 : ((AttrDispEntry)ans.getLast().getSecond()).order();
         Set<Holder<Attribute>> set = ans.stream().<Holder<Attribute>>map(Pair::getFirst).collect(Collectors.toSet());
         ArrayList<Reference<Attribute>> all = new ArrayList<>(BuiltInRegistries.ATTRIBUTE.holders().toList());
         all.sort(Comparator.comparing(ex -> ex.key().location().toString()));

         for (Reference<Attribute> e : all) {
            if (!set.contains(e)) {
               AttributeInstance ins = le.getAttribute(e);
               if (ins != null && (!ins.getModifiers().isEmpty() || disp == AttrDispEntry.AttributeDisplay.ALL)) {
                  ans.add(Pair.of(e, new AttrDispEntry(false, ++latest, 0.0)));
               }
            }
         }
      }

      return ans;
   }

   public enum AttributeDisplay {
      COMMON,
      ALL,
      ALL_EXCEPT_UNCHANGED;
   }
}
