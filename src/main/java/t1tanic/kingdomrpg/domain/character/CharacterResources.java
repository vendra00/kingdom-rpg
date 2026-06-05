package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tracks the current values of a character's dynamic, mutable pools and capacities.
 * <p>This class is designed to be embedded within a character entity using JPA
 * ({@link Embeddable}), mapping the current vitals directly to the host entity's table.
 * Unlike maximum capacity formulas, these fields represent the changing real-time state
 * of a character during gameplay (e.g., losing health in combat or spending mana on spells).</p>
 * * @author t1tanic
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
public class CharacterResources {
    /**
     * The character's current hit points.
     * Represents life force; reaching zero typically results in death or incapacitation.
     */
    private int health;
    /**
     * The character's current magical energy pool.
     * Consumed when casting spells or activating magical abilities.
     */
    private int mana;
    /**
     * The character's current physical endurance pool.
     * Consumed by physical activities such as sprinting, dodging, or heavy physical attacks.
     */
    private int stamina;
    /**
     * The total weight of all items currently carried by the character.
     * Measured against the maximum carrying capacity to determine encumbrance penalties.
     */
    private int carryWeight;

    /**
     * The amount of gold coins currently held by the character.
     * Players spend gold on bribes and purchases; NPCs accumulate it through successful bribes
     * and can be pickpocketed.
     */
    private int gold;
}