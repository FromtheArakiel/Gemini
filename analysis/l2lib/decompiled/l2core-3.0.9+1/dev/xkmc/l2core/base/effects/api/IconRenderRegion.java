package dev.xkmc.l2core.base.effects.api;

public record IconRenderRegion(float x, float y, float scale) {
   public static IconRenderRegion identity() {
      return new IconRenderRegion(0.0F, 0.0F, 1.0F);
   }

   public static IconRenderRegion of(int r, int ix, int iy, int w, int h) {
      float y = ((r - h) / 2.0F + iy) / r;
      float x = ((r - w) / 2.0F + ix) / r;
      return new IconRenderRegion(x, y, 1.0F / r);
   }

   public IconRenderRegion resize(IconRenderRegion inner) {
      return new IconRenderRegion(this.x + inner.x() * this.scale, this.y + inner.y() * this.scale, this.scale * inner.scale);
   }
}
