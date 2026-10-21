package dev.arakiel.gemini.component.api;

import net.minecraft.core.HolderLookup.Provider;

public interface CopyableComponent<C extends Component> extends Component {
   void copyFrom(C var1, Provider var2);
}
