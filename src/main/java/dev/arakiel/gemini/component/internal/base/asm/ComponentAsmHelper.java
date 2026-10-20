package dev.arakiel.gemini.component.internal.base.asm;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.ComponentContainer;
import dev.arakiel.gemini.component.ComponentKey;
import dev.arakiel.gemini.component.internal.base.AbstractComponentContainer;
import dev.arakiel.gemini.component.internal.base.QualifiedComponentFactory;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodHandles.Lookup.ClassOption;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ComponentAsmHelper {
   public static final boolean DEBUG_CLASSES = Boolean.getBoolean("gemini.debug.asm");
   public static final int ASM_VERSION = 589824;
   public static final String COMPONENT = Type.getInternalName(Component.class);
   public static final String COMPONENT_CONTAINER = Type.getInternalName(ComponentContainer.class);
   public static final String COMPONENT_TYPE = Type.getInternalName(ComponentKey.class);
   public static final String DYNAMIC_COMPONENT_CONTAINER_IMPL = Type.getInternalName(AbstractComponentContainer.class);
   public static final String IDENTIFIER = FabricLoader.getInstance()
      .getMappingResolver()
      .mapClassName("intermediary", "net.minecraft.class_2960")
      .replace('.', '/');
   public static final String EVENT = Type.getInternalName(Event.class);
   public static final String STATIC_COMPONENT_CONTAINER = createClassName("GeneratedComponentContainer");
   public static final String STATIC_CONTAINER_GETTER_DESC = "()L" + COMPONENT + ";";
   public static final String STATIC_COMPONENT_TYPE = createClassName("ComponentType");
   public static final String STATIC_CONTAINER_FACTORY = createClassName("GeneratedContainerFactory");
   public static final String ABSTRACT_COMPONENT_CONTAINER_CTOR_DESC;
   private static final List<ComponentAsmHelper.AsmGeneratedCallbackInfo> asmGeneratedCallbacks = findAsmComponentCallbacks();
   private static final AtomicInteger nextDebugId;

   private static List<ComponentAsmHelper.AsmGeneratedCallbackInfo> findAsmComponentCallbacks() {
      List<ComponentAsmHelper.AsmGeneratedCallbackInfo> asmGeneratedCallbacks = new ArrayList<>();

      for (Method containerMethod : ComponentContainer.class.getMethods()) {
         AsmGeneratedCallback annotation = containerMethod.getAnnotation(AsmGeneratedCallback.class);
         if (annotation != null) {
            Class<? extends Component> componentClass = annotation.value();
            boolean found = false;

            for (Method componentMethod : componentClass.getDeclaredMethods()) {
               if (componentMethod.isAnnotationPresent(CalledByAsm.class)) {
                  asmGeneratedCallbacks.add(new ComponentAsmHelper.AsmGeneratedCallbackInfo(containerMethod.getName(), componentClass, componentMethod.getName()));
                  found = true;
               }
            }

            if (!found) {
               throw new IllegalStateException("No ASM-called method found in " + componentClass);
            }
         }
      }

      return asmGeneratedCallbacks;
   }

   public static String createClassName(String name) {
      return ComponentAsmHelper.class.getPackageName().replace('.', '/') + "/_generated_$" + name;
   }

   public static Class<?> generateClass(ClassNode classNode, boolean hidden, @Nullable Object classData) throws IOException {
      ClassWriter writer = new ClassWriter(2);
      classNode.accept(writer);
      return generateClass(writer, classNode.name, hidden, classData);
   }

   private static Class<?> generateClass(ClassWriter classWriter, String className, boolean hidden, @Nullable Object classData) throws IOException {
      try {
         if (!hidden && classData != null) {
            throw new IllegalArgumentException("Class data is only supported for hidden classes");
         } else {
            byte[] bytes = classWriter.toByteArray();
            if (DEBUG_CLASSES) {
               ClassReader classReader = new ClassReader(bytes);
               classReader.accept(new CheckClassAdapter(null), 0);
               Path path = Paths.get(classReader.getClassName() + "_" + nextDebugId.getAndIncrement() + ".class");
               Files.createDirectories(path.getParent());
               Files.write(path, bytes);
            }

            if (hidden) {
               return classData == null
                  ? MethodHandles.lookup().defineHiddenClass(bytes, false, ClassOption.STRONG).lookupClass()
                  : MethodHandles.lookup().defineHiddenClassWithClassData(bytes, classData, false, ClassOption.STRONG).lookupClass();
            } else {
               return MethodHandles.lookup().defineClass(bytes);
            }
         }
      } catch (IllegalArgumentException | IllegalStateException | IOException var7) {
         throw new IOException("Failed to generate class " + className, var7);
      } catch (IllegalAccessException var8) {
         throw new StaticComponentLoadingException("Failed to define class " + className, var8);
      }
   }

   public static String getJavaIdentifierName(ResourceLocation identifier) {
      return identifier.toString().replace(':', '$').replace('/', '$').replace('.', '¤').replace('-', '£');
   }

   public static String getStaticStorageGetterName(ResourceLocation identifier) {
      return "get$" + getJavaIdentifierName(identifier);
   }

   public static Method findSam(Class<?> callbackClass) {
      if (!callbackClass.isInterface()) {
         throw badFunctionalInterface(callbackClass);
      } else {
         Method ret = null;

         for (Method m : callbackClass.getMethods()) {
            if (Modifier.isAbstract(m.getModifiers())) {
               if (ret != null) {
                  throw badFunctionalInterface(callbackClass);
               }

               ret = m;
            }
         }

         if (ret == null) {
            throw badFunctionalInterface(callbackClass);
         } else {
            return ret;
         }
      }
   }

   private static IllegalArgumentException badFunctionalInterface(Class<?> callbackClass) {
      return new IllegalArgumentException(callbackClass + " is not a functional interface!");
   }

   @Deprecated(
      forRemoval = true
   )
   public static <I> Class<? extends ComponentContainer> spinComponentContainer(
      Class<? super I> componentFactoryType, Map<ComponentKey<?>, I> componentFactories, Map<ComponentKey<?>, Class<? extends Component>> componentImpls
   ) throws IOException {
      Map<ComponentKey<?>, QualifiedComponentFactory<I>> merged = new LinkedHashMap<>();

      for (Entry<ComponentKey<?>, I> entry : componentFactories.entrySet()) {
         merged.put(entry.getKey(), new QualifiedComponentFactory<>(entry.getValue(), componentImpls.get(entry.getKey()), Set.of()));
      }

      return spinComponentContainer(componentFactoryType, merged);
   }

   public static <I> Class<? extends ComponentContainer> spinComponentContainer(
      Class<? super I> componentFactoryType, Map<ComponentKey<?>, QualifiedComponentFactory<I>> componentFactories
   ) throws IOException {
      ComponentBootstrap.INSTANCE.ensureInitialized();
      QualifiedComponentFactory.checkDependenciesSatisfied(componentFactories);
      Map<ComponentKey<?>, QualifiedComponentFactory<I>> sorted = QualifiedComponentFactory.sort(componentFactories);
      String containerImplName = STATIC_COMPONENT_CONTAINER + "Impl";
      String componentFactoryName = Type.getInternalName(componentFactoryType);
      Method sam = findSam(componentFactoryType);
      String samDescriptor = Type.getMethodDescriptor(sam);
      Class<?>[] factoryArgs = sam.getParameterTypes();
      Type[] actualCtorArgs = new Type[factoryArgs.length];

      for (int i = 0; i < factoryArgs.length; i++) {
         actualCtorArgs[i] = Type.getType(factoryArgs[i]);
      }

      String ctorDesc = Type.getMethodDescriptor(Type.VOID_TYPE, actualCtorArgs);
      ClassNode classNode = new ClassNode(589824);
      classNode.visit(61, 17, containerImplName, null, STATIC_COMPONENT_CONTAINER, null);
      String factoryFieldDescriptor = Type.getDescriptor(componentFactoryType);
      classNode.visitField(26, "componentKeys", "Ljava/util/Set;", "Ljava/util/Set<Ldev/arakiel/gemini/component/api/v3/component/ComponentKey<*>;>;", null);
      MethodVisitor keys = classNode.visitMethod(
         1, "keys", "()Ljava/util/Set;", "()Ljava/util/Set<Ldev/arakiel/gemini/component/api/v3/component/ComponentKey<*>;>;", null
      );
      keys.visitFieldInsn(178, containerImplName, "componentKeys", "Ljava/util/Set;");
      keys.visitInsn(176);
      keys.visitEnd();
      MethodVisitor hasComponents = classNode.visitMethod(1, "hasComponents", "()Z", null, null);
      hasComponents.visitCode();
      hasComponents.visitInsn(sorted.isEmpty() ? 3 : 4);
      hasComponents.visitInsn(172);
      hasComponents.visitEnd();
      MethodVisitor init = classNode.visitMethod(1, "<init>", ctorDesc, null, null);
      init.visitCode();
      init.visitVarInsn(25, 0);
      init.visitMethodInsn(183, STATIC_COMPONENT_CONTAINER, "<init>", ABSTRACT_COMPONENT_CONTAINER_CTOR_DESC, false);
      Map<ComponentAsmHelper.AsmGeneratedCallbackInfo, MethodVisitor> callbackMethods = new LinkedHashMap<>();

      for (ComponentAsmHelper.AsmGeneratedCallbackInfo callbackInfo : asmGeneratedCallbacks) {
         MethodVisitor visitor = classNode.visitMethod(1, callbackInfo.containerCallbackName(), "()V", null, null);
         visitor.visitCode();
         callbackMethods.put(callbackInfo, visitor);
      }

      for (Entry<ComponentKey<?>, QualifiedComponentFactory<I>> entry : sorted.entrySet()) {
         ResourceLocation identifier = entry.getKey().getId();
         String componentFieldName = getJavaIdentifierName(identifier);
         Class<? extends Component> impl = entry.getValue().impl();
         String componentFieldDescriptor = Type.getDescriptor(impl);
         String factoryFieldName = getFactoryFieldName(identifier);
         classNode.visitField(26, factoryFieldName, factoryFieldDescriptor, null, null).visitEnd();
         classNode.visitField(18, componentFieldName, componentFieldDescriptor, null, null).visitEnd();
         init.visitFieldInsn(178, containerImplName, factoryFieldName, factoryFieldDescriptor);

         for (int i = 0; i < factoryArgs.length; i++) {
            init.visitVarInsn(25, i + 1);
         }

         init.visitMethodInsn(185, componentFactoryName, sam.getName(), samDescriptor, true);
         init.visitLdcInsn("Component factory " + entry.getValue().factory().getClass() + " for " + identifier + " produced a null component");
         init.visitMethodInsn(184, "java/util/Objects", "requireNonNull", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", false);
         init.visitTypeInsn(192, Type.getInternalName(impl));
         init.visitVarInsn(25, 0);
         init.visitInsn(95);
         init.visitFieldInsn(181, containerImplName, componentFieldName, componentFieldDescriptor);
         MethodVisitor getter = classNode.visitMethod(1, getStaticStorageGetterName(identifier), STATIC_CONTAINER_GETTER_DESC, null, null);
         getter.visitVarInsn(25, 0);
         getter.visitFieldInsn(180, containerImplName, componentFieldName, componentFieldDescriptor);
         getter.visitInsn(176);
         getter.visitEnd();

         for (Entry<ComponentAsmHelper.AsmGeneratedCallbackInfo, MethodVisitor> e : callbackMethods.entrySet()) {
            if (e.getKey().componentClass().isAssignableFrom(impl)) {
               generateCallbackImpl(containerImplName, e.getValue(), componentFieldName, impl, componentFieldDescriptor, e.getKey().componentCallbackName());
            }
         }
      }

      init.visitInsn(177);
      init.visitEnd();

      for (Entry<ComponentAsmHelper.AsmGeneratedCallbackInfo, MethodVisitor> ex : callbackMethods.entrySet()) {
         ex.getValue().visitInsn(177);
         ex.getValue().visitEnd();
      }

      Object[] classData = new Object[sorted.size() + 1];
      classData[0] = Collections.unmodifiableSet(new ReferenceArraySet(sorted.keySet()));
      MethodVisitor clinit = classNode.visitMethod(8, "<clinit>", "()V", null, null);
      clinit.visitCode();
      ConstantDynamic constantClassData = constantClassData(Object[].class);
      clinit.visitLdcInsn(constantClassData);
      clinit.visitInsn(89);
      clinit.visitInsn(3);
      clinit.visitInsn(50);
      clinit.visitTypeInsn(192, Type.getInternalName(Set.class));
      clinit.visitFieldInsn(179, containerImplName, "componentKeys", Type.getDescriptor(Set.class));
      int i = 1;

      for (Entry<ComponentKey<?>, QualifiedComponentFactory<I>> entry : sorted.entrySet()) {
         classData[i] = entry.getValue().factory();
         clinit.visitInsn(89);
         clinit.visitLdcInsn(i);
         clinit.visitInsn(50);
         clinit.visitTypeInsn(192, Type.getInternalName(componentFactoryType));
         clinit.visitFieldInsn(179, containerImplName, getFactoryFieldName(entry.getKey().getId()), Type.getDescriptor(componentFactoryType));
         i++;
      }

      clinit.visitInsn(87);
      clinit.visitInsn(177);
      clinit.visitEnd();
      return generateClass(classNode, true, classData).asSubclass(ComponentContainer.class);
   }

   @NotNull
   public static ConstantDynamic constantClassData(Class<?> dataType) {
      return new ConstantDynamic(
         "_",
         Type.getDescriptor(dataType),
         new Handle(
            6,
            Type.getInternalName(MethodHandles.class),
            "classData",
            MethodType.methodType(Object.class, Lookup.class, String.class, Class.class).descriptorString(),
            false
         ),
         new Object[0]
      );
   }

   private static void generateCallbackImpl(
      String containerImplName, MethodVisitor tick, String componentFieldName, Class<? extends Component> impl, String componentFieldDescriptor, String target
   ) {
      tick.visitVarInsn(25, 0);
      tick.visitFieldInsn(180, containerImplName, componentFieldName, componentFieldDescriptor);
      if (impl.isInterface()) {
         tick.visitMethodInsn(185, Type.getInternalName(impl), target, "()V", true);
      } else {
         tick.visitMethodInsn(182, Type.getInternalName(impl), target, "()V", false);
      }
   }

   private static String getFactoryFieldName(ResourceLocation identifier) {
      return getJavaIdentifierName(identifier) + "$factory";
   }

   static {
      try {
         ABSTRACT_COMPONENT_CONTAINER_CTOR_DESC = Type.getConstructorDescriptor(AbstractComponentContainer.class.getConstructor());
      } catch (NoSuchMethodException var1) {
         throw new IllegalStateException("Failed to find one or more method descriptors", var1);
      }

      nextDebugId = new AtomicInteger();
   }

   static record AsmGeneratedCallbackInfo(String containerCallbackName, Class<? extends Component> componentClass, String componentCallbackName) {
   }
}
