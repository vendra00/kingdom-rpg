package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the profile and narrative identity details of a character.
 * <p>This class is designed to be embedded within a character entity using JPA
 * ({@link Embeddable}), mapping the character's biographical traits directly into
 * the host entity's database table. It encapsulates purely descriptive, non-statistical
 * background details.</p>
 * * @author t1tanic
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterIdentity {

    /**
     * The fantasy race of the character (e.g., Human, Elf, Dwarf).
     */
    private String race;

    /**
     * The professional archetype or occupational class of the character (e.g., Warrior, Mage, Rogue).
     */
    private String characterClass;

    /**
     * The gender identity of the character.
     */
    private String gender;

    /**
     * The historical origin story or social background of the character (e.g., Noble, Acolyte, Criminal).
     */
    private String background;
}
