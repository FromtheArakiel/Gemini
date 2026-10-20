package dev.arakiel.gemini.client;

import dev.arakiel.gemini.config.internal.ConfigHubScreen;
import net.minecraft.client.gui.screens.Screen;

import java.lang.reflect.Proxy;

public final class GeminiModMenu {
    private GeminiModMenu() {
    }

    public static Object create() {
        try {
            ClassLoader loader = GeminiModMenu.class.getClassLoader();
            Class<?> factoryType = Class.forName(
                    "com.terraformersmc.modmenu.api.ConfigScreenFactory",
                    false,
                    loader
            );
            Class<?> apiType = Class.forName(
                    "com.terraformersmc.modmenu.api.ModMenuApi",
                    false,
                    loader
            );
            Object factory = Proxy.newProxyInstance(loader, new Class<?>[]{factoryType}, (proxy, method, args) -> {
                if (method.getName().equals("create") && args != null && args.length == 1
                        && args[0] instanceof Screen parent) {
                    return new ConfigHubScreen(parent);
                }
                return objectMethod(proxy, method, args);
            });
            return Proxy.newProxyInstance(loader, new Class<?>[]{apiType}, (proxy, method, args) -> {
                if (method.getName().equals("getModConfigScreenFactory")) {
                    return factory;
                }
                return objectMethod(proxy, method, args);
            });
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("ModMenu is not available", exception);
        }
    }

    private static Object objectMethod(Object proxy, java.lang.reflect.Method method, Object[] args) {
        return switch (method.getName()) {
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> "Gemini ModMenu integration";
            default -> null;
        };
    }
}
