package dev.arakiel.gemini.capability;

import dev.arakiel.gemini.Gemini;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapabilityRegistryTest {
    private static final CapabilityKey<String> TEST_KEY =
            CapabilityKey.create(Gemini.id("test/capability"));

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void keyIdentityUsesRegistryId() {
        CapabilityKey<Integer> first = CapabilityKey.create(Gemini.id("test/key"));
        CapabilityKey<Long> second = CapabilityKey.create(Gemini.id("test/key"));
        CapabilityKey<Long> different = CapabilityKey.create(Gemini.id("test/other"));

        assertEquals(first, second);
        assertNotEquals(first, different);
    }

    @Test
    void providerPriorityAndRegistrationOrderAreStable() {
        CapabilityRegistry registry = new CapabilityRegistry();
        CapabilityContext context = context();
        registry.registerItemStack(TEST_KEY, ignored -> true, ignored -> Optional.empty(), CapabilityPriority.HIGHEST);
        registry.registerItemStack(TEST_KEY, ignored -> true, ignored -> Optional.of("normal"), CapabilityPriority.NORMAL);
        registry.registerItemStack(TEST_KEY, ignored -> true, ignored -> Optional.of("high"), CapabilityPriority.HIGH);
        registry.registerItemStack(TEST_KEY, ignored -> true, ignored -> Optional.of("high-later"), CapabilityPriority.HIGH);

        assertEquals(Optional.of("high"), registry.find(TEST_KEY, context));
        assertEquals(List.of("high", "high-later", "normal"), registry.findAll(TEST_KEY, context));
    }

    @Test
    void providerFailureDoesNotStopLookup() {
        CapabilityRegistry registry = new CapabilityRegistry();
        registry.registerItemStack(TEST_KEY, ignored -> true, ignored -> {
            throw new IllegalStateException("broken provider");
        }, CapabilityPriority.HIGH);
        registry.registerItemStack(
                TEST_KEY,
                ignored -> true,
                ignored -> Optional.of("fallback"),
                CapabilityPriority.LOW
        );

        assertEquals(Optional.of("fallback"), registry.find(TEST_KEY, context()));
    }

    @Test
    void firstLookupSealsRegistrations() {
        CapabilityRegistry registry = new CapabilityRegistry();
        registry.find(TEST_KEY, context());

        assertTrue(registry.isSealed());
        assertThrows(
                IllegalStateException.class,
                () -> registry.registerItemStack(
                        TEST_KEY,
                        ignored -> true,
                        ignored -> Optional.of("late"),
                        CapabilityPriority.NORMAL
                )
        );
    }

    @Test
    void handlesCanInvalidateAndUnregister() {
        CapabilityRegistry registry = new CapabilityRegistry();
        CapabilityProviderHandle handle = registry.registerItemStack(
                TEST_KEY,
                ignored -> true,
                ignored -> Optional.of("value"),
                CapabilityPriority.NORMAL
        );

        assertTrue(handle.isRegistered());
        handle.invalidate();
        assertTrue(handle.unregister());
        assertFalse(handle.isRegistered());
        assertEquals(Optional.empty(), registry.find(TEST_KEY, context()));
    }

    private static ItemCapabilityContext context() {
        return new ItemCapabilityContext(ItemStack.EMPTY, null, null);
    }
}
