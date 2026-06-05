package t1tanic.kingdomrpg.domain.character.enums;

/**
 * Playable character class archetypes available during character creation.
 * <p>{@link #id()} returns the lowercase key stored in the database and used by
 * {@code CantripRepository.findByAllowedClassesContaining}.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CharacterClass {
    MAGE   ("mage"),
    CLERIC ("cleric"),
    PALADIN("paladin"),
    ROGUE  ("rogue"),
    RANGER ("ranger"),
    WARRIOR("warrior");

    private final String id;

    CharacterClass(String id) { this.id = id; }

    /** Lowercase key stored in the database and matched by {@code LIKE %id%} queries. */
    public String id() { return id; }

    /**
     * Parses a raw string into a {@code CharacterClass}, falling back to {@link #WARRIOR} if unrecognised.
     *
     * @param s the raw class name from user input or the database
     * @return the matching constant, or {@code WARRIOR} as the default
     */
    public static CharacterClass fromString(String s) {
        if (s == null) return WARRIOR;
        for (CharacterClass cc : values()) {
            if (cc.id.equalsIgnoreCase(s.trim())) return cc;
        }
        return WARRIOR;
    }
}
