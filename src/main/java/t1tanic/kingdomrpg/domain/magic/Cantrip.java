package t1tanic.kingdomrpg.domain.magic;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.character.enums.CharacterClass;
import t1tanic.kingdomrpg.domain.character.enums.CharacterClassSetConverter;
import t1tanic.kingdomrpg.domain.enums.DamageType;
import t1tanic.kingdomrpg.domain.magic.enums.CantripEffect;

import java.util.Set;

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
     * The unique, machine-readable string identifier for the cantrip (e.g., "fire_bolt").
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
     * The set of character classes allowed to learn this cantrip.
     * Persisted as a comma-separated lowercase string (e.g., {@code "mage,rogue"}) via
     * {@link CharacterClassSetConverter}, keeping the column compatible with LIKE-based queries.
     */
    @Convert(converter = CharacterClassSetConverter.class)
    private Set<CharacterClass> allowedClasses;
    /**
     * The elemental or physical damage type dealt by this spell.
     * {@code null} for utility, buff, and healing cantrips.
     */
    @Enumerated(EnumType.STRING)
    private DamageType damageType;
    /**
     * The dice notation formula for baseline damage (e.g., {@code "1d8"}, {@code "2d4"}).
     * {@code null} for utility, buff, and healing cantrips.
     */
    private String damageDice;
    /**
     * The functional effect category triggered upon successful resolution of the cantrip.
     */
    @Enumerated(EnumType.STRING)
    private CantripEffect effect;
}
