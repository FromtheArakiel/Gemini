package dev.xkmc.l2core.serial.advancements;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.l2core.compat.patchouli.PatchouliHelper;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Advancement.Builder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.conditions.ICondition;

public class AdvancementGenerator {
   private final RegistrateAdvancementProvider pvd;
   private final String modid;

   public AdvancementGenerator(RegistrateAdvancementProvider pvd, String modid) {
      this.pvd = pvd;
      this.modid = modid;
   }

   private record EntryData(String id, ItemStack item, List<IAdvBuilder> builder, String title, String desc) {
      EntryData(String id, ItemStack item, IAdvBuilder builder, String title, String desc) {
         this(id, item, new ArrayList<>(List.of(builder)), title, desc);
      }
   }

   public class TabBuilder {
      private final String tab;
      private final ResourceLocation bg;
      private AdvancementGenerator.TabBuilder.Entry root;

      public TabBuilder(String tab) {
         this.tab = tab;
         this.bg = ResourceLocation.fromNamespaceAndPath(AdvancementGenerator.this.modid, "textures/gui/advancements/backgrounds/" + tab + ".png");
      }

      public void build() {
         this.root.build();
      }

      public AdvancementGenerator.TabBuilder.Entry root(String id, Item item, CriterionBuilder builder, String title, String desc) {
         return this.root(id, item.getDefaultInstance(), builder, title, desc);
      }

      public AdvancementGenerator.TabBuilder.Entry root(String id, ItemStack item, CriterionBuilder builder, String title, String desc) {
         if (this.root == null) {
            this.root = new AdvancementGenerator.TabBuilder.Entry(new AdvancementGenerator.EntryData(id, item, builder, title, desc), null);
         }

         return this.root;
      }

      public AdvancementGenerator.TabBuilder.Entry hidden(String id, CriterionBuilder builder) {
         return new AdvancementGenerator.TabBuilder.Entry(new AdvancementGenerator.EntryData(id, ItemStack.EMPTY, builder, "", ""), null);
      }

      public class Entry {
         private final List<AdvancementGenerator.TabBuilder.Entry> children = new ArrayList<>();
         private final AdvancementGenerator.EntryData data;
         private final ResourceLocation rl;
         private final AdvancementGenerator.TabBuilder.Entry parent;
         private AdvancementType type = AdvancementType.TASK;
         private boolean showToast = true;
         private boolean announce = true;
         private boolean hidden = false;
         private AdvancementHolder result;

         private Entry(AdvancementGenerator.EntryData data, @Nullable AdvancementGenerator.TabBuilder.Entry parent) {
            this.data = data;
            this.parent = parent;
            if (parent == null) {
               this.showToast = false;
               this.announce = false;
               this.rl = TabBuilder.this.bg;
            } else {
               this.rl = null;
            }
         }

         public AdvancementGenerator.TabBuilder.Entry create(String id, Item item, CriterionBuilder builder, String title, String desc) {
            return this.create(id, item.getDefaultInstance(), builder, title, desc);
         }

         public AdvancementGenerator.TabBuilder.Entry create(String id, ItemStack item, CriterionBuilder builder, String title, String desc) {
            AdvancementGenerator.TabBuilder.Entry sub = TabBuilder.this.new Entry(new AdvancementGenerator.EntryData(id, item, builder, title, desc), this);
            this.children.add(sub);
            return sub;
         }

         public AdvancementGenerator.TabBuilder.Entry patchouli(L2Registrate reg, CriterionBuilder builder, PatchouliHelper pat, String title, String desc) {
            ItemStack stack = PatchouliHelper.getBook(pat.book);
            return this.create("patchouli", stack, builder, title, desc).add(new ModLoadedAdv("patchouli")).add(pat.reward);
         }

         public AdvancementGenerator.TabBuilder.Entry root() {
            return TabBuilder.this.root;
         }

         public AdvancementGenerator.TabBuilder.Entry enter() {
            return this.children.getLast();
         }

         public AdvancementGenerator.TabBuilder.Entry type(AdvancementType type) {
            this.type = type;
            return this;
         }

         public AdvancementGenerator.TabBuilder.Entry type(AdvancementType type, boolean showToast, boolean announce, boolean hidden) {
            this.type = type;
            this.showToast = showToast;
            this.announce = announce;
            this.hidden = hidden;
            return this;
         }

         public AdvancementGenerator.TabBuilder.Entry add(IAdvBuilder builder) {
            this.data.builder.add(builder);
            return this;
         }

         public void build() {
            Builder builder = Builder.advancement();
            if (!this.data.item.isEmpty()) {
               builder.display(
                  this.data.item,
                  AdvancementGenerator.this.pvd.title(AdvancementGenerator.this.modid, TabBuilder.this.tab + "." + this.data.id, this.data.title),
                  AdvancementGenerator.this.pvd.desc(AdvancementGenerator.this.modid, TabBuilder.this.tab + "." + this.data.id, this.data.desc),
                  this.rl,
                  this.type,
                  this.showToast,
                  this.announce,
                  this.hidden
               );
            }

            if (this.parent != null) {
               builder.parent(this.parent.result);
            }

            String uid = AdvancementGenerator.this.modid + ":" + TabBuilder.this.tab + "/" + this.data.id;
            List<ICondition> conditions = new ArrayList<>();

            for (IAdvBuilder e : this.data.builder) {
               e.onBuild(uid, builder, conditions);
            }

            Consumer<AdvancementHolder> cons = (Consumer<AdvancementHolder>)(conditions.isEmpty()
               ? AdvancementGenerator.this.pvd
               : exx -> AdvancementGenerator.this.pvd.withConditions(exx, conditions));
            this.result = builder.save(cons, uid);

            for (AdvancementGenerator.TabBuilder.Entry e : this.children) {
               e.build();
            }
         }

         public void finish() {
            TabBuilder.this.build();
         }
      }
   }
}
