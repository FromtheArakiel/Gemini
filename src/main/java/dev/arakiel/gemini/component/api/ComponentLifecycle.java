package dev.arakiel.gemini.component.api;

import dev.arakiel.gemini.component.internal.base.asm.CalledByAsm;

public final class ComponentLifecycle {
    private ComponentLifecycle() {
    }

    public interface ServerTicking extends Component {
        @CalledByAsm
        void serverTick();
    }

    public interface ClientTicking extends Component {
        @CalledByAsm
        void clientTick();
    }

    public interface CommonTicking extends ServerTicking, ClientTicking {
        @Override
        default void clientTick() {
            tick();
        }

        @Override
        default void serverTick() {
            tick();
        }

        void tick();
    }

    public interface ServerLoad extends Component {
        @CalledByAsm
        void loadServerside();
    }

    public interface ServerUnload extends Component {
        @CalledByAsm
        void unloadServerside();
    }

    public interface ClientLoad extends Component {
        @CalledByAsm
        void loadClientside();
    }

    public interface ClientUnload extends Component {
        @CalledByAsm
        void unloadClientside();
    }
}
