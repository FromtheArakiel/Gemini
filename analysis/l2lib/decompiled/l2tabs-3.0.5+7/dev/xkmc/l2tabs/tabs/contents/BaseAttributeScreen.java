package dev.xkmc.l2tabs.tabs.contents;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2tabs.init.data.AttrDispEntry;
import dev.xkmc.l2tabs.init.data.L2TabsConfig;
import dev.xkmc.l2tabs.init.data.L2TabsLangData;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public abstract class BaseAttributeScreen extends BaseTextScreen {
   private final int page;

   private static int getSize() {
      return (Integer)L2TabsConfig.CLIENT.attributeLinePerPage.get();
   }

   protected BaseAttributeScreen(Component title, int page) {
      super(title, ResourceLocation.fromNamespaceAndPath("l2tabs", "textures/gui/empty.png"));
      this.page = page;
   }

   public abstract LivingEntity getEntity();

   @Override
   public void init() {
      super.init();
      int w = 10;
      int h = 11;
      int size = AttrDispEntry.get(this.getEntity()).size();
      int totalPage = (size - 1) / getSize() + 1;
      int x = (this.width + this.imageWidth) / 2 - 16;
      int y = (this.height - this.imageHeight) / 2 + 4;
      if (this.page > 0) {
         this.addRenderableWidget(Button.builder(Component.literal("<"), e -> this.click(this.page - 1)).pos(x - w - 1, y).size(w, h).build());
      }

      if (this.page < totalPage - 1) {
         this.addRenderableWidget(Button.builder(Component.literal(">"), e -> this.click(this.page + 1)).pos(x, y).size(w, h).build());
      }
   }

   protected abstract void click(int var1);

   public void render(GuiGraphics g, int mx, int my, float ptick) {
      super.render(g, mx, my, ptick);
      this.render(g, mx, my, ptick, this.getEntity(), AttrDispEntry.get(this.getEntity()));
   }

   public void render(GuiGraphics g, int mx, int my, float ptick, LivingEntity player, List<Pair<Holder<Attribute>, AttrDispEntry>> list) {
      int x = this.leftPos + 8;
      int y = this.topPos + 6;
      Pair<Holder<Attribute>, AttrDispEntry> focus = null;
      int count = 0;

      for (Pair<Holder<Attribute>, AttrDispEntry> entry : list) {
         count++;
         if (count > this.page * getSize() && count <= (this.page + 1) * getSize()) {
            double val = player.getAttributeValue((Holder)entry.getFirst());
            Component comp = Component.translatable(
               "attribute.modifier.equals." + (((AttrDispEntry)entry.getSecond()).usePercent() ? 1 : 0),
               new Object[]{
                  ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(((AttrDispEntry)entry.getSecond()).usePercent() ? val * 100.0 : val),
                  Component.translatable(((Attribute)((Holder)entry.getFirst()).value()).getDescriptionId())
               }
            );
            g.drawString(this.font, comp, x, y, 0, false);
            if (mx > x && mx < x + this.font.width(comp) && my > y && my < y + 10) {
               focus = entry;
            }

            y += 10;
         }
      }

      if (focus != null) {
         g.renderComponentTooltip(this.font, this.getAttributeDetail(player, focus), mx, my);
      }
   }

   public List<Component> getAttributeDetail(LivingEntity entity, Pair<Holder<Attribute>, AttrDispEntry> entry) {
      List<Component> ans = this.getAttributeDetail(entity, (Holder<Attribute>)entry.getFirst());
      if (((AttrDispEntry)entry.getSecond()).intrinsic() != 0.0) {
         ans.add(L2TabsLangData.INTRINSIC.get(number("%s", ((AttrDispEntry)entry.getSecond()).intrinsic())).withStyle(ChatFormatting.BLUE));
      }

      return ans;
   }

   public List<Component> getAttributeDetail(LivingEntity entity, Holder<Attribute> attr) {
      AttributeInstance ins = entity.getAttribute(attr);
      if (ins == null) {
         return List.of();
      }

      Set<AttributeModifier> all = ins.getModifiers();
      ArrayList<AttributeModifier> adds = new ArrayList<>();
      ArrayList<AttributeModifier> m0s = new ArrayList<>();
      ArrayList<AttributeModifier> m1s = new ArrayList<>();

      for (AttributeModifier e : all) {
         ArrayList<AttributeModifier> x = switch (e.operation()) {
            case ADD_VALUE -> adds;
            case ADD_MULTIPLIED_BASE -> m0s;
            case ADD_MULTIPLIED_TOTAL -> m1s;
            default -> throw new MatchException(null, null);
         };
         x.add(e);
      }

      double base = ins.getBaseValue();
      double addv = 0.0;
      double m0v = 0.0;
      double m1v = 1.0;

      for (AttributeModifier e : adds) {
         addv += e.amount();
      }

      for (AttributeModifier e : m0s) {
         m0v += e.amount();
      }

      for (AttributeModifier e : m1s) {
         m1v *= 1.0 + e.amount();
      }

      double total = (base + addv) * (1.0 + m0v) * m1v;
      List<Component> ans = new ArrayList<>();
      ans.add(Component.translatable(((Attribute)attr.value()).getDescriptionId()).withStyle(ChatFormatting.GOLD));
      boolean shift = Screen.hasShiftDown();
      ans.add(L2TabsLangData.BASE.get(number("%s", base)).withStyle(ChatFormatting.BLUE));
      ans.add(L2TabsLangData.ADD.get(numberSigned("%s", addv)).withStyle(ChatFormatting.BLUE));
      if (shift) {
         for (AttributeModifier e : adds) {
            ans.add(numberSigned("%s", e.amount()).append(name(e)));
         }
      }

      ans.add(L2TabsLangData.MULT_BASE.get(numberSigned("%s%%", m0v * 100.0)).withStyle(ChatFormatting.BLUE));
      if (shift) {
         for (AttributeModifier e : m0s) {
            ans.add(numberSigned("%s%%", e.amount() * 100.0).append(name(e)));
         }
      }

      ans.add(L2TabsLangData.MULT_TOTAL.get(number("x%s", m1v)).withStyle(ChatFormatting.BLUE));
      if (shift) {
         for (AttributeModifier e : m1s) {
            ans.add(number("x%s", 1.0 + e.amount()).append(name(e)));
         }
      }

      ans.add(L2TabsLangData.FORMAT.get(number("%s", base), numberSigned("%s", addv), numberSigned("%s", m0v), number("%s", m1v), number("%s", total)));
      if (!shift) {
         ans.add(L2TabsLangData.DETAIL.get().withStyle(ChatFormatting.GRAY));
      }

      return ans;
   }

   private static MutableComponent number(String base, double value) {
      return Component.literal(String.format(base, ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value))).withStyle(ChatFormatting.GREEN);
   }

   private static MutableComponent numberSigned(String base, double value) {
      return value >= 0.0
         ? Component.literal(String.format("+" + base, ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(value))).withStyle(ChatFormatting.GREEN)
         : Component.literal(String.format("-" + base, ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-value))).withStyle(ChatFormatting.RED);
   }

   private static MutableComponent name(AttributeModifier e) {
      return Component.literal("  (" + e.id() + ")").withStyle(ChatFormatting.DARK_GRAY);
   }
}
