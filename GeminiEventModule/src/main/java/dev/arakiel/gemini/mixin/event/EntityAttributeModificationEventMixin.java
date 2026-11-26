package dev.arakiel.gemini.mixin.event;

import dev.arakiel.gemini.event.GeminiEvents;
import dev.arakiel.gemini.event.entity.EntityAttributeModificationEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;

@Mixin(DefaultAttributes.class)
public abstract class EntityAttributeModificationEventMixin {
    @Shadow
    @Mutable
    private static Map<EntityType<?>, AttributeSupplier> SUPPLIERS;

    @Unique
    private static boolean gemini$attributesPosted;

    @Inject(method = "getSupplier", at = @At("HEAD"))
    private static void gemini$modifyAttributes(
            EntityType<? extends LivingEntity> type,
            CallbackInfoReturnable<AttributeSupplier> callback
    ) {
        if (gemini$attributesPosted) {
            return;
        }
        gemini$attributesPosted = true;
        Map<EntityType<?>, AttributeSupplier.Builder> builders = new HashMap<>();
        SUPPLIERS.forEach((entityType, supplier) ->
                builders.put(entityType, gemini$copyBuilder(supplier)));
        EntityAttributeModificationEvent event = new EntityAttributeModificationEvent(builders);
        GeminiEvents.post(event);
        Map<EntityType<?>, AttributeSupplier> modified = new HashMap<>();
        builders.forEach((entityType, builder) ->
                modified.put(entityType, builder.build()));
        SUPPLIERS = modified;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private static AttributeSupplier.Builder gemini$copyBuilder(AttributeSupplier supplier) {
        AttributeSupplier.Builder builder = new AttributeSupplier.Builder();
        try {
            Field field = AttributeSupplier.class.getDeclaredField("instances");
            field.setAccessible(true);
            Map<Holder<Attribute>, AttributeInstance> instances =
                    (Map<Holder<Attribute>, AttributeInstance>) field.get(supplier);
            instances.forEach((attribute, instance) -> builder.add(attribute, instance.getBaseValue()));
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to copy default entity attributes", exception);
        }
        return builder;
    }
}
