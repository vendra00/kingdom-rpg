package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.character.enums.Attribute;

/**
 * Represents the core attributes of a character in the RPG domain.
 * <p>This class is designed to be embedded within a character entity using JPA
 * ({@link Embeddable}), mapping individual attribute values to the host entity's table.
 * It includes standard RPG attributes, a dynamic getter via an {@link Attribute} enum,
 * and D&D-style modifier calculations.</p>
 * * @author t1tanic
 * @version 1.0
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterAttributes {

    /**
     * Measures physical power and muscular force. Default value is 10.
     */
    private int strength     = 10;
    /**
     * Measures agility, reflexes, and balance. Default value is 10.
     */
    private int dexterity    = 10;
    /**
     * Measures health, stamina, and vital force. Default value is 10.
     */
    private int constitution = 10;
    /**
     * Measures mental acuity, information recall, and analytical skill. Default value is 10.
     */
    private int intelligence = 10;
    /**
     * Measures awareness, intuition, and insight. Default value is 10.
     */
    private int wisdom       = 10;
    /**
     * Measures force of personality, persuasiveness, and leadership. Default value is 10.
     */
    private int charisma     = 10;

    /**
     * Retrieves the raw value of a specific attribute using the {@link Attribute} enum.
     *
     * @param attr the attribute type to retrieve
     * @return the integer value of the requested attribute
     * @throws NullPointerException if the provided attribute is null
     */
    public int get(Attribute attr) {
        return switch (attr) {
            case STRENGTH     -> strength;
            case DEXTERITY    -> dexterity;
            case CONSTITUTION -> constitution;
            case INTELLIGENCE -> intelligence;
            case WISDOM       -> wisdom;
            case CHARISMA     -> charisma;
        };
    }

    /**
     * Calculates the modifier for a specific attribute type.
     *
     * @param attr the attribute type whose modifier is to be calculated
     * @return the calculated modifier value based on the current attribute score
     */
    public int modifier(Attribute attr) {
        return modifier(get(attr));
    }

    /**
     * Static utility method to calculate a D&D-style attribute modifier.
     * <p>The formula applied is: {@code floor((value - 10) / 2)}</p>
     * * <p>Example values:</p>
     * <ul>
     * <li>Score 10 or 11 -> Modifier 0</li>
     * <li>Score 12 or 13 -> Modifier +1</li>
     * <li>Score 8 or 9   -> Modifier -1</li>
     * </ul>
     *
     * @param value the raw attribute score
     * @return the calculated modifier as an integer
     */
    public static int modifier(int value) {
        return (int) Math.floor((value - 10) / 2.0);
    }
}
