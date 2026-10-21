package com.tterrag.registrate.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;

public interface RegistrateLookupFillerProvider extends RegistrateProvider {
   CompletableFuture<Provider> getFilledProvider();
}
