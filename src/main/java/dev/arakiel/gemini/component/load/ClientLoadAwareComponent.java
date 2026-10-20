package dev.arakiel.gemini.component.load;

import dev.arakiel.gemini.component.Component;
import dev.arakiel.gemini.component.internal.base.asm.CalledByAsm;

public interface ClientLoadAwareComponent extends Component {
   @CalledByAsm
   void loadClientside();
}
