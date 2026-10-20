package dev.arakiel.gemini.component.internal.base.asm;

import com.google.common.annotations.VisibleForTesting;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.StaticComponentInitializer;
import dev.arakiel.gemini.component.internal.base.LazyDispatcher;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nullable;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ComponentBootstrap extends LazyDispatcher {
   public static final String COMPONENT_TYPE_INIT_DESC = Type.getMethodDescriptor(
      Type.VOID_TYPE, new Type[]{Type.getObjectType(ComponentAsmHelper.IDENTIFIER), Type.getType(Class.class)}
   );
   public static final String COMPONENT_TYPE_GET0_DESC = "(L" + ComponentAsmHelper.COMPONENT_CONTAINER + ";)L" + ComponentAsmHelper.COMPONENT + ";";
   public static final String STATIC_INIT_ENTRYPOINT = "gemini:static-components";
   public static final ComponentBootstrap INSTANCE = new ComponentBootstrap();
   private final List<EntrypointContainer<StaticComponentInitializer>> staticComponentInitializers = FabricLoader.getInstance()
      .getEntrypointContainers("gemini:static-components", StaticComponentInitializer.class);
   @VisibleForTesting
   Collection<ResourceLocation> additionalComponentIds = new ArrayList<>();
   private Map<ResourceLocation, Class<? extends ComponentKey<?>>> generatedComponentTypes = new HashMap<>();

   public ComponentBootstrap() {
      super("registering a ComponentType");
   }

   public boolean isGenerated(Class<?> keyClass) {
      return this.requiresInitialization() ? false : this.generatedComponentTypes.containsValue(keyClass);
   }

   @Nullable
   public Class<? extends ComponentKey<?>> getGeneratedComponentTypeClass(ResourceLocation componentId) {
      this.ensureInitialized();

      assert this.generatedComponentTypes != null;

      return this.generatedComponentTypes.get(componentId);
   }

   @Override
   protected void init() {
      try {
         Set<ResourceLocation> staticComponentTypes = new TreeSet<>(Comparator.comparing(ResourceLocation::toString));

         for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();
            if (metadata.containsCustomValue("gemini:components")) {
               try {
                  for (CustomValue value : metadata.getCustomValue("gemini:components").getAsArray()) {
                     staticComponentTypes.add(ResourceLocation.parse(value.getAsString()));
                  }
               } catch (ResourceLocationException | ClassCastException var8) {
                  throw new StaticComponentLoadingException(
                     "Failed to load component ids declared by " + metadata.getName() + "(" + metadata.getId() + ")", var8
                  );
               }
            }
         }

         for (EntrypointContainer<StaticComponentInitializer> staticInitializer : this.staticComponentInitializers) {
            try {
               staticComponentTypes.addAll(((StaticComponentInitializer)staticInitializer.getEntrypoint()).getSupportedComponentKeys());
            } catch (Throwable var7) {
               ModMetadata badMod = staticInitializer.getProvider().getMetadata();
               throw new StaticComponentLoadingException(
                  String.format("Exception while querying %s (%s) for supported static component types", badMod.getName(), badMod.getId()), var7
               );
            }
         }

         staticComponentTypes.addAll(this.additionalComponentIds);
         this.spinStaticContainerItf(staticComponentTypes);
         this.generatedComponentTypes = this.spinStaticComponentKeys(staticComponentTypes);
      } catch (UncheckedIOException | IOException var9) {
         throw new StaticComponentLoadingException("Failed to load statically defined components", var9);
      }
   }

   @Override
   protected void postInit() {
      for (EntrypointContainer<StaticComponentInitializer> staticInitializer : this.staticComponentInitializers) {
         ((StaticComponentInitializer)staticInitializer.getEntrypoint()).finalizeStaticBootstrap();
      }
   }

   private Map<ResourceLocation, Class<? extends ComponentKey<?>>> spinStaticComponentKeys(Set<ResourceLocation> staticComponentKeys) throws IOException {
      Map<ResourceLocation, Class<? extends ComponentKey<?>>> generatedComponentTypes = new HashMap<>(staticComponentKeys.size());

      for (ResourceLocation componentId : staticComponentKeys) {
         ClassNode componentTypeWriter = new ClassNode(589824);
         String componentTypeName = ComponentAsmHelper.STATIC_COMPONENT_TYPE + "Impl";
         componentTypeWriter.visit(52, 17, componentTypeName, null, ComponentAsmHelper.COMPONENT_TYPE, null);
         MethodVisitor init = componentTypeWriter.visitMethod(1, "<init>", COMPONENT_TYPE_INIT_DESC, null, null);
         init.visitCode();
         init.visitVarInsn(25, 0);
         init.visitVarInsn(25, 1);
         init.visitVarInsn(25, 2);
         init.visitMethodInsn(183, ComponentAsmHelper.COMPONENT_TYPE, "<init>", COMPONENT_TYPE_INIT_DESC, false);
         init.visitInsn(177);
         init.visitEnd();
         MethodVisitor get = componentTypeWriter.visitMethod(4, "getInternal", COMPONENT_TYPE_GET0_DESC, null, null);
         get.visitCode();
         get.visitVarInsn(25, 1);
         get.visitTypeInsn(192, ComponentAsmHelper.STATIC_COMPONENT_CONTAINER);
         get.visitMethodInsn(
            182,
            ComponentAsmHelper.STATIC_COMPONENT_CONTAINER,
            ComponentAsmHelper.getStaticStorageGetterName(componentId),
            ComponentAsmHelper.STATIC_CONTAINER_GETTER_DESC,
            false
         );
         get.visitInsn(176);
         get.visitEnd();
         Class<? extends ComponentKey<?>> ct = (Class<? extends ComponentKey<?>>)ComponentAsmHelper.generateClass(componentTypeWriter, true, null);
         generatedComponentTypes.put(componentId, ct);
      }

      return generatedComponentTypes;
   }

   private void spinStaticContainerItf(Set<ResourceLocation> staticComponentTypes) throws IOException {
      ClassNode staticContainerWriter = new ClassNode(589824);
      staticContainerWriter.visit(52, 1025, ComponentAsmHelper.STATIC_COMPONENT_CONTAINER, null, ComponentAsmHelper.DYNAMIC_COMPONENT_CONTAINER_IMPL, null);
      MethodVisitor init = staticContainerWriter.visitMethod(1, "<init>", ComponentAsmHelper.ABSTRACT_COMPONENT_CONTAINER_CTOR_DESC, null, null);
      init.visitCode();
      init.visitVarInsn(25, 0);
      init.visitMethodInsn(183, ComponentAsmHelper.DYNAMIC_COMPONENT_CONTAINER_IMPL, "<init>", ComponentAsmHelper.ABSTRACT_COMPONENT_CONTAINER_CTOR_DESC, false);
      init.visitInsn(177);
      init.visitEnd();

      for (ResourceLocation componentId : staticComponentTypes) {
         MethodVisitor methodWriter = staticContainerWriter.visitMethod(
            1, ComponentAsmHelper.getStaticStorageGetterName(componentId), ComponentAsmHelper.STATIC_CONTAINER_GETTER_DESC, null, null
         );
         methodWriter.visitInsn(1);
         methodWriter.visitInsn(176);
         methodWriter.visitEnd();
      }

      staticContainerWriter.visitEnd();
      ComponentAsmHelper.generateClass(staticContainerWriter, false, null);
   }
}
