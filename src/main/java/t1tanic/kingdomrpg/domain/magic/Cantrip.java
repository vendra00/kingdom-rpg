package t1tanic.kingdomrpg.domain.magic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.magic.enums.CantripEffect;

/**
 * Represents a Cantrip (a basic, infinitely castable level-0 spell) within the RPG domain.
 * <p>Unlike high-tier spells that consume mana slots, cantrips serve as foundational magical abilities
 * for characters. This entity maps directly to the persistent data store, tracking combat parameters,
 * class availability restrictions, and specialized behavioral properties handled via an effect hook.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cantrip {
    /**
     * The unique, machine-readable string identifier for the cantrip (e.g., "fire-bolt").
     */
    @Id
    private String id;
    /**
     * The human-readable name of the cantrip displayed in UI elements (e.g., "Fire Bolt").
     */
    private String name;
    /**
     * The magical school of wizardry or tradition the spell belongs to (e.g., "Evocation", "Illusion").
     */
    private String school;
    /**
     * A descriptive narrative detailing the spell's behavioral outcomes and flavor elements.
     * Stored with an expanded length boundary to accommodate detailed flavor text.
     */
    @Column(length = 500)
    private String description;
    /**
     * Comma-separated list of professional archetype identifiers allowed to learn this cantrip.
     * <p>Example: {@code "mage,rogue"}</p>
     */
    private String allowedClasses;
    /**
     * The type of elemental or physical damage dealt by this spell.
     * Retains a {@code null} value if the cantrip is utility-focused or non-damaging.
     * <p>Example: {@code "Fire"}, {@code "Necrotic"}</p>
     */
    private String damageType;
    /**
     * The dice notation calculation formula dictating baseline damage resolution tracking.
     * Retains a {@code null} value if the cantrip is utility-focused or non-damaging.
     * <p>Example: {@code "1d8"}, {@code "2d4"}</p>
     */
    private String damageDice;
    /**
     * The specialized status modifier, behavioral mechanic, or operational state flag triggered
     * upon successful resolution of the cantrip.
     */
    @Enumerated(EnumType.STRING)
    private CantripEffect effect;
}
