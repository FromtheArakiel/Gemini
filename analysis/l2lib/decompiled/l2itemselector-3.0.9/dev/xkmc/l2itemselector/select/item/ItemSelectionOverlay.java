package dev.xkmc.l2itemselector.select.item;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2itemselector.overlay.ItemSelSideBar;
import dev.xkmc.l2itemselector.overlay.SelectionSideBar;
import dev.xkmc.l2itemselector.overlay.SideBar;
import dev.xkmc.l2itemselector.overlay.TextBox;
import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemSelectionOverlay extends ItemSelSideBar<ItemSelectionOverlay.ItemSelSignature> {
   public static final ItemSelectionOverlay INSTANCE = new ItemSelectionOverlay();
   private static final ResourceLocation EMPTY = ResourceLocation.withDefaultNamespace("empty");

   private ItemSelectionOverlay() {
      super(40.0F, 3.0F);
   }

   @Override
   public Pair<List<ItemStack>, Integer> getItems() {
      LocalPlayer player = Proxy.getClientPlayer();
      if (player == null) {
         return Pair.of(List.of(), 0);
      }

      IItemSelector.Holder sel = IItemSelector.getSelection(player);
      assert sel != null;
      return Pair.of(sel.getDisplayList(), sel.getIndex(player));
   }

   public boolean isAvailable(ItemStack itemStack) {
      return true;
   }

   @Override
   public boolean onCenter() {
      return false;
   }

   public ItemSelectionOverlay.ItemSelSignature getSignature() {
      LocalPlayer player = Proxy.getClientPlayer();
      if (player == null) {
         return new ItemSelectionOverlay.ItemSelSignature(EMPTY, 0, 0);
      }

      IItemSelector.Holder sel = IItemSelector.getSelection(Proxy.getClientPlayer());
      return sel == null
         ? new ItemSelectionOverlay.ItemSelSignature(EMPTY, 0, 0)
         : new ItemSelectionOverlay.ItemSelSignature(sel.selector().getID(), sel.getIndex(player), sel.getSelHash());
   }

   @Override
   public boolean isScreenOn() {
      LocalPlayer player = Proxy.getClientPlayer();
      return player == null ? false : ItemSelectionListener.INSTANCE.isClientActive(player);
   }

   @Override
   protected void renderEntry(SelectionSideBar.Context ctx, ItemStack stack, int i, int selected) {
      int y = 18 * i + ctx.y0();
      this.renderSelection(ctx.g(), ctx.x0(), y, 64, this.isAvailable(stack), selected == i);
      if (this.ease_time == this.max_ease) {
         TextBox box = new TextBox(ctx.g(), 0, 1, ctx.x0() + 22, y + 8, -1);
         box.renderLongText(ctx.font(), List.of(stack.getHoverName()));
      }

      ctx.renderItem(stack, ctx.x0(), y);
   }

   @Override
   protected int getXOffset(int width) {
      float progress = (this.max_ease - this.ease_time) / this.max_ease;
      return Math.round(this.onCenter() ? width / 2.0F - 54.0F - 1.0F - progress * width / 2.0F : 2.0F - progress * 20.0F);
   }

   @Override
   protected int getYOffset(int height) {
      LocalPlayer player = Proxy.getClientPlayer();
      assert player != null;
      IItemSelector.Holder sel = IItemSelector.getSelection(player);
      assert sel != null;
      return Math.round(height / 2.0F - 9 * sel.getList().size() + 1.0F);
   }

   public record ItemSelSignature(ResourceLocation id, int val, int hash) implements SideBar.Signature<ItemSelectionOverlay.ItemSelSignature> {
      public boolean shouldRefreshIdle(SideBar<?> sideBar, @Nullable ItemSelectionOverlay.ItemSelSignature old) {
         return !this.equals(old);
      }
   }
}
