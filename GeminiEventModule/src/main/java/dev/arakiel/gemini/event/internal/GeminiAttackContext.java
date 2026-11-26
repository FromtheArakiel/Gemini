package dev.arakiel.gemini.event.internal;

import dev.arakiel.gemini.event.combat.CriticalHitEvent;
import dev.arakiel.gemini.event.combat.SweepAttackEvent;

public interface GeminiAttackContext {
    CriticalHitEvent gemini$getCriticalHitEvent();

    void gemini$setCriticalHitEvent(CriticalHitEvent event);

    SweepAttackEvent gemini$getSweepAttackEvent();

    void gemini$setSweepAttackEvent(SweepAttackEvent event);
}
