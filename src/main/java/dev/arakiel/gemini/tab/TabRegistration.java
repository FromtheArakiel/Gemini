package dev.arakiel.gemini.tab;

import java.util.Objects;

public record TabRegistration(TabHandle handle, Status status) {
    public TabRegistration {
        Objects.requireNonNull(handle, "handle");
        Objects.requireNonNull(status, "status");
    }

    public boolean successful() {
        return this.status == Status.REGISTERED || this.status == Status.ALREADY_REGISTERED;
    }

    public enum Status {
        REGISTERED,
        ALREADY_REGISTERED,
        CONFLICT,
        TOO_LATE
    }
}
