package dev.arakiel.gemini.energy.compat.teamreborn;

import dev.arakiel.gemini.energy.GeminiEnergy;
import dev.arakiel.gemini.energy.SimpleEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import team.reborn.energy.api.EnergyStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamRebornEnergyCompatTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void oneGeminiEnergyEqualsOneTeamRebornEnergy() {
        assertEquals(1L, GeminiEnergy.TE_TO_GE);
        assertEquals(1L, GeminiEnergy.GE_TO_TE);
    }

    @Test
    void geminiStorageCommitsAndAbortsTeamRebornTransactions() {
        SimpleEnergyStorage storage = new SimpleEnergyStorage(100, 100, 100);
        GeminiEnergyStorageAdapter adapter = new GeminiEnergyStorageAdapter(storage);

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(40, adapter.insert(40, transaction));
            transaction.abort();
        }
        assertEquals(0, storage.amount());

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(40, adapter.insert(40, transaction));
            transaction.commit();
        }
        assertEquals(40, storage.amount());

        try (Transaction transaction = Transaction.openOuter()) {
            assertEquals(15, adapter.extract(15, transaction));
            transaction.commit();
        }
        assertEquals(25, storage.amount());
    }

    @Test
    void teamRebornStorageWrapsIntoGeminiStorage() {
        EnergyStorage teamStorage = new team.reborn.energy.api.base.SimpleEnergyStorage(100, 50, 30);
        TeamRebornEnergyView view = new TeamRebornEnergyView(teamStorage);

        assertEquals(50, view.receive(50, true));
        assertEquals(0, view.amount());
        assertEquals(50, view.receive(50, false));
        assertEquals(30, view.extract(100, false));
        assertEquals(20, view.amount());
    }
}
