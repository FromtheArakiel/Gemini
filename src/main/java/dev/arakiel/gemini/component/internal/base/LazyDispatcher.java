package dev.arakiel.gemini.component.internal.base;

import org.jetbrains.annotations.ApiStatus.OverrideOnly;

public abstract class LazyDispatcher {
   private volatile LazyDispatcher.State state = LazyDispatcher.State.UNLOADED;
   private final String likelyInitTrigger;

   protected LazyDispatcher(String likelyInitTrigger) {
      this.likelyInitTrigger = likelyInitTrigger;
   }

   public void ensureInitialized() {
      if (this.state == LazyDispatcher.State.ERRED) {
         throw new IllegalStateException("Initialization failed, check the log above for likely causes");
      } else {
         if (this.state != LazyDispatcher.State.LOADED) {
            synchronized (this) {
               if (this.state == LazyDispatcher.State.LOADING) {
                  this.onCircularLoading();
               } else if (this.state == LazyDispatcher.State.UNLOADED) {
                  this.state = LazyDispatcher.State.LOADING;

                  try {
                     this.init();
                     this.state = LazyDispatcher.State.LOADED;
                     this.postInit();
                  } catch (Throwable var4) {
                     this.state = LazyDispatcher.State.ERRED;
                     ComponentsInternals.LOGGER.fatal("[Gemini Components] Initialization failed: ", var4);
                     throw var4;
                  }
               }
            }
         }
      }
   }

   protected void onCircularLoading() {
      throw new IllegalStateException("Circular loading issue, a mod is probably " + this.likelyInitTrigger + " in the wrong place");
   }

   protected boolean requiresInitialization() {
      return this.state == LazyDispatcher.State.UNLOADED;
   }

   @OverrideOnly
   protected abstract void init();

   public void checkLoading(Class<?> ownerClass, String methodName) {
      if (this.state != LazyDispatcher.State.LOADING) {
         throw new IllegalStateException(ownerClass.getSimpleName() + "#" + methodName + " called at the wrong time");
      }
   }

   protected void postInit() {
   }

   static enum State {
      UNLOADED,
      LOADING,
      LOADED,
      ERRED;
   }
}
