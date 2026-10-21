package dev.arakiel.gemini.event;

public abstract class GeminiEvent {
    private boolean canceled;
    private EventResult result = EventResult.DEFAULT;

    public boolean isCancelable() {
        return getClass().isAnnotationPresent(Cancelable.class);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        if (canceled && !isCancelable()) {
            throw new UnsupportedOperationException("Event is not cancelable: " + getClass().getName());
        }
        this.canceled = canceled;
    }

    public boolean hasResult() {
        return getClass().isAnnotationPresent(HasResult.class);
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }
}
