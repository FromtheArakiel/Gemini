package dev.arakiel.gemini.component.tick;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.internal.base.asm.CalledByAsm;

public interface ServerTickingComponent extends Component {
   @CalledByAsm
   void serverTick();
}
