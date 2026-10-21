package dev.arakiel.gemini.component.internal.token;

import dev.arakiel.gemini.Gemini;
import dev.arakiel.gemini.component.api.ConditionalToken;
import dev.arakiel.gemini.component.api.TokenKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ConditionalTokenTest {
    @Test
    void tokensAreCreatedReusedAndExpiredByTicks() {
        ConditionalData data = new ConditionalData(null);
        TokenKey<ExpiringToken> key = key("expiring", ExpiringToken::new);

        ExpiringToken token = data.getOrCreate(key);
        token.remaining = 2;
        assertSame(token, data.getOrCreate(key));

        data.serverTick();
        assertNotNull(data.get(key));
        data.serverTick();
        assertNull(data.get(key));
    }

    @Test
    void tokensRoundTripThroughNbt() {
        ConditionalData original = new ConditionalData(null);
        TokenKey<ExpiringToken> key = key("nbt", ExpiringToken::new);
        original.getOrCreate(key).remaining = 17;

        CompoundTag tag = new CompoundTag();
        HolderLookup.Provider registries = RegistryAccess.EMPTY;
        original.writeToNbt(tag, registries);

        ConditionalData decoded = new ConditionalData(null);
        decoded.readFromNbt(tag, registries);
        assertEquals(17, decoded.get(key).remaining);
    }

    @Test
    void respawnOnlyCopiesRetainedTokens() {
        TokenKey<ExpiringToken> retained = key("retained", () -> new ExpiringToken(true));
        TokenKey<ExpiringToken> removed = key("removed", ExpiringToken::new);
        ConditionalData original = new ConditionalData(null);
        original.getOrCreate(retained);
        original.getOrCreate(removed);
        assertEquals(2, original.keys().size());
        org.junit.jupiter.api.Assertions.assertTrue(original.get(retained).retainOnDeath(null));

        ConditionalData copy = new ConditionalData(null);
        copy.copyForRespawn(original, RegistryAccess.EMPTY, false, false, true);

        assertEquals(1, copy.keys().size());
        assertNotNull(copy.get(retained));
        assertNull(copy.get(removed));

        ConditionalData liveCopy = new ConditionalData(null);
        liveCopy.copyForRespawn(original, RegistryAccess.EMPTY, true, false, true);
        assertEquals(2, liveCopy.keys().size());
        assertNotNull(liveCopy.get(removed));
    }

    private static TokenKey<ExpiringToken> key(String path, java.util.function.Supplier<ExpiringToken> factory) {
        return new TokenKey<>(Gemini.id(path), ExpiringToken.class, factory);
    }

    private static final class ExpiringToken extends ConditionalToken {
        private final boolean retain;
        private int remaining = 1;

        private ExpiringToken() {
            this(false);
        }

        private ExpiringToken(boolean retain) {
            this.retain = retain;
        }

        @Override
        public boolean tick(Player player) {
            return --remaining <= 0;
        }

        @Override
        public boolean retainOnDeath(Player player) {
            return retain;
        }

        @Override
        public void readFromNbt(CompoundTag tag, HolderLookup.Provider registries) {
            remaining = tag.getInt("remaining");
        }

        @Override
        public void writeToNbt(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putInt("remaining", remaining);
        }
    }
}
