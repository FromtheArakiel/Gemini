package dev.arakiel.gemini.attribute;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeminiDamageTypeDefinitionTest {
    @Test
    void allCustomDamageTypeResourcesExist() {
        assertNotNull(resource("bleed"));
        assertNotNull(resource("soul_flame"));
        assertNotNull(resource("freeze"));
        assertNotNull(resource("life_sync"));
        assertNotNull(resource("void_eye"));
        assertNotNull(resource("emerald"));
        assertEquals(14, GeminiDamageTypes.ATTACK_VARIANTS.size());

        for (var key : GeminiDamageTypes.ATTACK_VARIANTS) {
            assertNotNull(resource(key.location().getPath()));
        }
    }

    private static java.net.URL resource(String path) {
        return GeminiDamageTypes.class.getClassLoader()
                .getResource("data/gemini/damage_type/" + path + ".json");
    }
}
