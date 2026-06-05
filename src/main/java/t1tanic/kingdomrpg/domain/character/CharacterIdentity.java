package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.character.enums.CharacterBackground;
import t1tanic.kingdomrpg.domain.character.enums.CharacterBackgroundConverter;
import t1tanic.kingdomrpg.domain.character.enums.CharacterClass;
import t1tanic.kingdomrpg.domain.character.enums.CharacterClassConverter;
import t1tanic.kingdomrpg.domain.character.enums.CharacterRace;
import t1tanic.kingdomrpg.domain.character.enums.CharacterRaceConverter;

/**
 * Represents the profile and narrative identity details of a character.
 * <p>Embedded directly into the host entity's table. All three typed fields use JPA
 * {@link Convert} to store lowercase string ids (e.g. {@code "halforc"}, {@code "mage"},
 * {@code "outlander"}), keeping values human-readable and compatible with the frontend payload.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterIdentity {

    /** The fantasy race of the character. Stored as a normalised lowercase id (e.g. {@code "halforc"}). */
    @Convert(converter = CharacterRaceConverter.class)
    private CharacterRace race;

    /** The professional archetype of the character. Stored as a lowercase id (e.g. {@code "mage"}). */
    @Convert(converter = CharacterClassConverter.class)
    private CharacterClass characterClass;

    /** The gender identity of the character. */
    private String gender;

    /** The historical background of the character. Stored as a lowercase name (e.g. {@code "outlander"}). */
    @Convert(converter = CharacterBackgroundConverter.class)
    private CharacterBackground background;
}
