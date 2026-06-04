package t1tanic.kingdomrpg.domain.character;

/**
 * Enumerates the six core character attributes used throughout the RPG system.
 * <p>These attributes align with classic d20/D&D mechanics, governing physical power,
 * physical resilience, mental prowess, and social influence. The enum includes utility
 * methods for generating localized UI text and handling serialized network payloads.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum Attribute {

    /**
     * Measures physical power, muscle mass, and raw force.
     */
    STRENGTH,

    /**
     * Measures agility, reflexes, balance, and fine motor skills.
     */
    DEXTERITY,

    /**
     * Measures health, stamina, vital energy, and structural resilience.
     */
    CONSTITUTION,

    /**
     * Measures raw mental acuity, analytical capabilities, and information recall.
     */
    INTELLIGENCE,

    /**
     * Measures situational awareness, intuition, empathy, and practical insight.
     */
    WISDOM,

    /**
     * Measures force of personality, structural leadership, persuasiveness, and charm.
     */
    CHARISMA;

    /**
     * Returns a standard three-letter uppercase abbreviation used for UI display layouts.
     * <p>Examples: "STR", "DEX", "CON"</p>
     *
     * @return a 3-character uppercase {@link String} representing the abbreviation
     */
    public String abbrev() { return name().substring(0, 3); }

    /**
     * Generates a lowercase identifier matching standard JSON payload property keys
     * transmitted by external client interfaces or APIs.
     * <p>Examples: "strength", "constitution"</p>
     *
     * @return a lowercase {@link String} key matching the attribute name
     */
    public String key() {
        return name().toLowerCase();
    }

    /**
     * Resolves a lowercase or case-insensitive string key back into its corresponding
     * {@code Attribute} enum constant.
     *
     * @param key the case-insensitive string key to map (e.g., "wisdom" or "WISDOM")
     * @return the matching {@code Attribute} constant
     * @throws IllegalArgumentException if the provided key does not map to any valid attribute
     * @throws NullPointerException if the provided key is null
     */
    public static Attribute fromKey(String key) {
        return valueOf(key.toUpperCase());
    }
}
