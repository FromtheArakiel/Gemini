package dev.arakiel.gemini.energy;

public record EnergyPushResult(
        long available,
        long extracted,
        long delivered,
        long rejected,
        int endpoints
) {
    public EnergyPushResult {
        if (available < 0 || extracted < 0 || delivered < 0 || rejected < 0 || endpoints < 0) {
            throw new IllegalArgumentException("Energy push values cannot be negative");
        }
        if (extracted > available || delivered > extracted) {
            throw new IllegalArgumentException("Invalid energy push accounting");
        }
    }

    public boolean movedEnergy() {
        return this.delivered > 0;
    }
}
