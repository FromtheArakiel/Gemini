package dev.arakiel.gemini.component.internal.base.asm;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentFactory;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.ComponentRegistrationInitializer;
import dev.arakiel.gemini.component.internal.base.LazyDispatcher;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

@SuppressWarnings({"unchecked", "deprecation"})
public abstract class StaticComponentPluginBase<T, I> extends LazyDispatcher {
   private final ComponentContainer.Factory.Builder<T> containerFactoryBuilder;

   protected StaticComponentPluginBase(String likelyInitTrigger, Class<T> providerClass) {
      super(likelyInitTrigger);
      this.containerFactoryBuilder = ComponentContainer.Factory.builder(providerClass);
   }

   public static <I> Class<? extends I> spinContainerFactory(
      Class<? super I> containerFactoryType, Class<? extends ComponentContainer> containerImpl, List<Class<?>> actualFactoryParams
   ) throws IOException {
      ComponentBootstrap.INSTANCE.ensureInitialized();
      Lookup lookup = MethodHandles.lookup();
      Constructor<?>[] constructors = containerImpl.getConstructors();
      if (constructors.length != 1) {
         throw new IllegalStateException("Ambiguous constructor declarations in " + containerImpl + ": " + Arrays.toString((Object[])constructors));
      } else {
         Method factorySam = ComponentAsmHelper.findSam(containerFactoryType);
         if (factorySam.getParameterCount() != actualFactoryParams.size()) {
            throw new IllegalArgumentException("Actual argument list length mismatches with factory SAM: " + actualFactoryParams + " and " + factorySam);
         } else if (constructors[0].getParameterCount() != factorySam.getParameterCount()) {
            throw new IllegalArgumentException(
               "Factory SAM parameter count should be the same as container constructor (found " + factorySam + " for " + constructors[0] + ")"
            );
         } else {
            Class<?>[] factoryParamClasses = factorySam.getParameterTypes();
            Type[] factoryArgs = new Type[factoryParamClasses.length];

            for (int i = 0; i < factoryParamClasses.length; i++) {
               if (!factoryParamClasses[i].isAssignableFrom(actualFactoryParams.get(i))) {
                  throw new IllegalArgumentException(
                     "Container factory parameter %s is not assignable from specified actual parameter %s(%s, %s)"
                        .formatted(factoryParamClasses[i].getSimpleName(), actualFactoryParams.get(i).getSimpleName(), factorySam, actualFactoryParams)
                  );
               }

               factoryArgs[i] = Type.getType(factoryParamClasses[i]);
            }

            ClassNode containerFactoryWriter = new ClassNode(589824);
            String factoryImplName = ComponentAsmHelper.STATIC_CONTAINER_FACTORY;
            containerFactoryWriter.visit(61, 17, factoryImplName, null, "java/lang/Object", new String[]{Type.getInternalName(containerFactoryType)});
            MethodVisitor init = containerFactoryWriter.visitMethod(1, "<init>", "()V", null, null);
            init.visitVarInsn(25, 0);
            init.visitMethodInsn(183, "java/lang/Object", "<init>", "()V", false);
            init.visitInsn(177);
            init.visitEnd();
            MethodVisitor createContainer = containerFactoryWriter.visitMethod(1, factorySam.getName(), Type.getMethodDescriptor(factorySam), null, null);
            Object constantClassData = ComponentAsmHelper.constantClassData(MethodHandle.class);
            createContainer.visitLdcInsn(constantClassData);

            for (int i = 0; i < actualFactoryParams.size(); i++) {
               createContainer.visitVarInsn(factoryArgs[i].getOpcode(21), i + 1);
               if (factoryArgs[i].getSort() == 10 || factoryArgs[i].getSort() == 9) {
                  createContainer.visitTypeInsn(192, Type.getInternalName(actualFactoryParams.get(i)));
               }
            }

            createContainer.visitMethodInsn(182, Type.getInternalName(MethodHandle.class), "invokeExact", Type.getMethodDescriptor(factorySam), false);
            createContainer.visitInsn(176);
            createContainer.visitEnd();
            containerFactoryWriter.visitEnd();

            MethodHandle containerCtor;
            try {
               containerCtor = lookup.findConstructor(containerImpl, MethodType.methodType(void.class, factorySam.getParameterTypes()))
                  .asType(MethodType.methodType(factorySam.getReturnType(), factorySam.getParameterTypes()));
            } catch (Throwable var14) {
               throw new RuntimeException("Could not find container constructor", var14);
            }

            return (Class<? extends I>)ComponentAsmHelper.generateClass(containerFactoryWriter, true, containerCtor);
         }
      }
   }

   public static ComponentContainer createEmptyContainer() {
      try {
         Class<? extends ComponentContainer> containerCls = ComponentAsmHelper.spinComponentContainer(Runnable.class, Collections.emptyMap());
         return containerCls.getConstructor().newInstance();
      } catch (ReflectiveOperationException | IOException var1) {
         throw new StaticComponentLoadingException("Failed to generate empty component container", var1);
      }
   }

   protected ComponentContainer.Factory<T> buildContainerFactory() {
      this.ensureInitialized();
      return this.containerFactoryBuilder.build();
   }

   @Override
   protected void init() {
      processInitializers(this.getEntrypoints(), this::dispatchRegistration);
   }

   public static <I> void processInitializers(Collection<EntrypointContainer<I>> entrypoints, Consumer<I> action) {
      for (EntrypointContainer<I> entrypoint : entrypoints) {
         try {
            action.accept((I)entrypoint.getEntrypoint());
         } catch (Throwable var6) {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            throw new StaticComponentLoadingException(
               String.format("Exception while registering static component factories for %s (%s)", metadata.getName(), metadata.getId()), var6
            );
         }
      }
   }

   public static <I extends ComponentRegistrationInitializer> Collection<EntrypointContainer<I>> getComponentEntrypoints(String key, Class<I> type) {
      Collection<EntrypointContainer<ComponentRegistrationInitializer>> generic = FabricLoader.getInstance()
         .getEntrypointContainers("gemini:components", ComponentRegistrationInitializer.class);
      Collection<EntrypointContainer<I>> specific = new ArrayList<>(FabricLoader.getInstance().getEntrypointContainers(key, type));

      for (EntrypointContainer<ComponentRegistrationInitializer> container : generic) {
         if (type.isInstance(container.getEntrypoint())) {
            specific.add((EntrypointContainer<I>)container);
         }
      }

      return specific;
   }

   protected abstract Collection<EntrypointContainer<I>> getEntrypoints();

   protected abstract void dispatchRegistration(I var1);

   protected <C extends Component> void register(ComponentKey<C> key, ComponentFactory<T, ? extends C> factory) {
      this.containerFactoryBuilder.component(key, factory);
   }

   protected <C extends Component> void register(ComponentKey<? super C> key, Class<C> impl, ComponentFactory<T, ? extends C> factory) {
      this.containerFactoryBuilder.component(key, impl, factory);
   }
}
