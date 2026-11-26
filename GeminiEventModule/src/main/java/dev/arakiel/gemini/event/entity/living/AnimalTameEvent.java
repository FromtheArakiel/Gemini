package dev.arakiel.gemini.event.entity.living;

import dev.arakiel.gemini.event.Cancelable;
import dev.arakiel.gemini.event.entity.EntityEvent;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

@Cancelable
public class AnimalTameEvent extends EntityEvent {
    private final Player player;

    public AnimalTameEvent(Animal animal, Player player) {
        super(animal);
        this.player = Objects.requireNonNull(player, "player");
    }

    public Animal getAnimal() {
        return (Animal) this.getEntity();
    }

    public Player getPlayer() {
        return this.player;
    }
}
