package dev.arakiel.gemini.component.internal.itemstack;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentFactoryRegistries;
import dev.arakiel.gemini.component.ComponentInitializers;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.asm.StaticComponentPluginBase;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.item.ItemStack;

public final class StaticItemStackComponentPlugin
        extends StaticComponentPluginBase<ItemStack, ComponentInitializers.ItemStack>
        implements ComponentFactoryRegistries.ItemStack {
    public static final StaticItemStackComponentPlugin INSTANCE =
            new StaticItemStackComponentPlugin();

    private final List<ComponentInitializers.ItemStack> testingInitializers;
    private ComponentContainer.Factory<ItemStack> builtFactory;

    private StaticItemStackComponentPlugin() {
        this(null);
    }

    StaticItemStackComponentPlugin(List<ComponentInitializers.ItemStack> testingInitializers) {
        super("accessing components on an ItemStack", ItemStack.class);
        this.testingInitializers = testingInitializers;
    }

    @Override
    protected Collection<EntrypointContainer<ComponentInitializers.ItemStack>> getEntrypoints() {
        return getComponentEntrypoints(
                "gemini:components-itemstack",
                ComponentInitializers.ItemStack.class
        );
    }

    @Override
    protected void dispatchRegistration(ComponentInitializers.ItemStack initializer) {
        initializer.registerItemStackComponentFactories(this);
    }

    @Override
    protected void init() {
        if (this.testingInitializers == null) {
            super.init();
            return;
        }
        this.testingInitializers.forEach(this::dispatchRegistration);
    }

    @Override
    public <C extends Component> void register(
            ComponentKey<C> key,
            ComponentFactory<ItemStack, ? extends C> factory
    ) {
        this.checkLoading(ComponentFactoryRegistries.ItemStack.class, "register");
        super.register(key, key.getComponentClass(), factory);
    }

    @Override
    public <C extends Component> void register(
            ComponentKey<? super C> key,
            Class<C> implementation,
            ComponentFactory<ItemStack, ? extends C> factory
    ) {
        this.checkLoading(ComponentFactoryRegistries.ItemStack.class, "register");
        super.register(key, implementation, factory);
    }

    @Override
    public <C extends Component> ComponentFactoryRegistries.ItemStack.Registration<C>
    beginRegistration(ComponentKey<C> key) {
        return new RegistrationImpl<>(key);
    }

    ComponentContainer.Factory<ItemStack> factory() {
        if (this.builtFactory == null) {
            synchronized (this) {
                if (this.builtFactory == null) {
                    this.ensureInitialized();
                    this.builtFactory = this.buildContainerFactory();
                }
            }
        }
        return this.builtFactory;
    }

    private final class RegistrationImpl<C extends Component>
            implements ComponentFactoryRegistries.ItemStack.Registration<C> {
        private final ComponentKey<? super C> key;
        private final Set<ComponentKey<?>> dependencies = new LinkedHashSet<>();
        private Class<C> implementation;

        private RegistrationImpl(ComponentKey<C> key) {
            this.key = key;
            this.implementation = key.getComponentClass();
        }

        @Override
        public ComponentFactoryRegistries.ItemStack.Registration<C> after(
                ComponentKey<?> dependency
        ) {
            this.dependencies.add(dependency);
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <I extends C> ComponentFactoryRegistries.ItemStack.Registration<I> impl(
                Class<I> implementation
        ) {
            this.implementation = (Class<C>) implementation;
            return (ComponentFactoryRegistries.ItemStack.Registration<I>) this;
        }

        @Override
        public void end(ComponentFactory<ItemStack, C> factory) {
            StaticItemStackComponentPlugin.this.checkLoading(
                    ComponentFactoryRegistries.ItemStack.Registration.class,
                    "end"
            );
            StaticItemStackComponentPlugin.this.register(
                    this.key,
                    this.implementation,
                    factory,
                    this.dependencies
            );
        }
    }
}
