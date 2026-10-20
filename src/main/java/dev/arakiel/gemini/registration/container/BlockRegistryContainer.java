package dev.arakiel.gemini.registration.container;

import dev.arakiel.gemini.registration.api.AutoRegistryContainer;
import dev.arakiel.gemini.registration.api.RegistrySink;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public interface BlockRegistryContainer extends AutoRegistryContainer<Block> {
    @Override
    default Registry<Block> getRegistry() {
        return BuiltInRegistries.BLOCK;
    }

    @Override
    default Class<Block> getTargetFieldType() {
        return Block.class;
    }

    default RegistrySink<Item> getItemSink() {
        return RegistrySink.from(BuiltInRegistries.ITEM);
    }

    @Override
    default void postProcessField(String namespace, Block value, String identifier, Field field) {
        if (field.isAnnotationPresent(NoBlockItem.class)) {
            return;
        }

        getItemSink().register(ResourceLocation.fromNamespaceAndPath(namespace, identifier), createBlockItem(value, identifier));
    }

    default BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Properties());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NoBlockItem {
    }
}
