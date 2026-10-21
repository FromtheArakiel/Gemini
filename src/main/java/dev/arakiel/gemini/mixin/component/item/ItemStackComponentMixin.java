package dev.arakiel.gemini.mixin.component.item;

import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentProvider;
import dev.arakiel.gemini.component.internal.itemstack.GeminiItemStackInternals;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackComponentMixin implements ComponentProvider {
    @Override
    public ComponentContainer getComponentContainer() {
        return GeminiItemStackInternals.getComponentContainer((ItemStack) (Object) this);
    }

    @Inject(method = "getComponents", at = @At("HEAD"))
    private void gemini$commitBeforeComponentRead(CallbackInfoReturnable<DataComponentMap> cir) {
        GeminiItemStackInternals.commit((ItemStack) (Object) this);
    }

    @Inject(method = "getComponentsPatch", at = @At("HEAD"))
    private void gemini$commitBeforePatchRead(CallbackInfoReturnable<DataComponentPatch> cir) {
        GeminiItemStackInternals.commit((ItemStack) (Object) this);
    }

    @Inject(method = "copy", at = @At("HEAD"))
    private void gemini$commitBeforeCopy(CallbackInfoReturnable<ItemStack> cir) {
        GeminiItemStackInternals.commit((ItemStack) (Object) this);
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void gemini$copyRuntimeComponents(CallbackInfoReturnable<ItemStack> cir) {
        GeminiItemStackInternals.copyRuntime(
                (ItemStack) (Object) this,
                cir.getReturnValue()
        );
    }

    @Inject(
            method = "save(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;",
            at = @At("HEAD")
    )
    private void gemini$commitBeforeSave(
            HolderLookup.Provider provider,
            Tag prefix,
            CallbackInfoReturnable<Tag> cir
    ) {
        GeminiItemStackInternals.commit((ItemStack) (Object) this, provider);
    }

    @Inject(
            method = "save(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/Tag;",
            at = @At("HEAD")
    )
    private void gemini$commitBeforeSave(
            HolderLookup.Provider provider,
            CallbackInfoReturnable<Tag> cir
    ) {
        GeminiItemStackInternals.commit((ItemStack) (Object) this, provider);
    }

    @Inject(method = "parse", at = @At("HEAD"))
    private static void gemini$pushDecodeProvider(
            HolderLookup.Provider provider,
            Tag tag,
            CallbackInfoReturnable<Optional<ItemStack>> cir
    ) {
        GeminiItemStackInternals.pushDecodeProvider(provider);
    }

    @Inject(method = "parse", at = @At("RETURN"))
    private static void gemini$popDecodeProvider(
            HolderLookup.Provider provider,
            Tag tag,
            CallbackInfoReturnable<Optional<ItemStack>> cir
    ) {
        GeminiItemStackInternals.popDecodeProvider();
    }

    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"))
    private static void gemini$commitBeforeComparison(
            ItemStack first,
            ItemStack second,
            CallbackInfoReturnable<Boolean> cir
    ) {
        GeminiItemStackInternals.prepareForComparison(first, second);
    }

    @Inject(method = "hashItemAndComponents", at = @At("HEAD"))
    private static void gemini$commitBeforeHash(
            @Nullable ItemStack stack,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (stack != null) {
            GeminiItemStackInternals.commit(stack);
        }
    }

    @Inject(
            method = "applyComponents(Lnet/minecraft/core/component/DataComponentPatch;)V",
            at = @At("RETURN")
    )
    private void gemini$invalidateAfterPatch(
            DataComponentPatch patch,
            CallbackInfo ci
    ) {
        GeminiItemStackInternals.invalidate((ItemStack) (Object) this);
    }

    @Inject(
            method = "applyComponentsAndValidate(Lnet/minecraft/core/component/DataComponentPatch;)V",
            at = @At("RETURN")
    )
    private void gemini$invalidateAfterValidatedPatch(
            DataComponentPatch patch,
            CallbackInfo ci
    ) {
        GeminiItemStackInternals.invalidate((ItemStack) (Object) this);
    }

    @Inject(
            method = "applyComponents(Lnet/minecraft/core/component/DataComponentMap;)V",
            at = @At("RETURN")
    )
    private void gemini$invalidateAfterComponentMap(
            DataComponentMap components,
            CallbackInfo ci
    ) {
        GeminiItemStackInternals.invalidate((ItemStack) (Object) this);
    }
}
