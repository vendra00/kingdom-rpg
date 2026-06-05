package t1tanic.kingdomrpg.domain.character.enums;

import java.util.Map;

import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Defines the playable character races in the RPG system and their respective starting attribute bonuses.
 * <p>Each race carries a normalised lowercase {@link #id()} for database storage and input parsing,
 * and a {@link #displayName()} for UI output. The {@link #fromString(String)} parser handles
 * variants such as "half-orc" and "Half_Orc" mapping to {@code HALFORC}.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CharacterRace {

    /**
     * Versatile and well-rounded. Receives a minor +1 bonus to all six core attributes.
     */
    HUMAN   ("human",    "Human",    Map.of(STRENGTH,1, DEXTERITY,1, CONSTITUTION,1, INTELLIGENCE,1, WISDOM,1, CHARISMA,1)),

    /**
     * Graceful and intellectually sharp. Receives +2 Dexterity and +1 Intelligence.
     */
    ELF     ("elf",      "Elf",      Map.of(DEXTERITY,2, INTELLIGENCE,1)),

    /**
     * Sturdy and physically powerful. Receives +2 Constitution and +1 Strength.
     */
    DWARF   ("dwarf",    "Dwarf",    Map.of(CONSTITUTION,2, STRENGTH,1)),

    /**
     * Exceptionally strong and resilient. Receives +2 Strength and +1 Constitution.
     */
    HALFORC ("halforc",  "Half-Orc", Map.of(STRENGTH,2, CONSTITUTION,1)),

    /**
     * Nimble and inherently expressive. Receives +2 Dexterity and +1 Charisma.
     */
    HALFLING("halfling", "Halfling", Map.of(DEXTERITY,2, CHARISMA,1)),

    /**
     * Alluring, cunning, and otherworldly. Receives +2 Charisma and +1 Intelligence.
     */
    TIEFLING("tiefling", "Tiefling", Map.of(CHARISMA,2, INTELLIGENCE,1));

    private final String               id;
    private final String               displayName;
    private final Map<Attribute, Integer> bonuses;

    CharacterRace(String id, String displayName, Map<Attribute, Integer> b) {
        this.id          = id;
        this.displayName = displayName;
        this.bonuses     = b;
    }

    /** Normalised lowercase key stored in the database (e.g. {@code "halforc"}). */
    public String id() { return id; }

    /** Capitalised display name for UI output (e.g. {@code "Half-Orc"}). */
    public String displayName() { return displayName; }

    /**
     * Retrieves the map of attribute type keys to their corresponding integer bonus modifiers.
     *
     * @return an unmodifiable {@link Map} containing the structural attribute adjustments for this race
     */
    public Map<Attribute, Integer> getBonuses() { return bonuses; }

    /**
     * Safely parses a string into its matching {@code CharacterRace} constant.
     * Normalises input by stripping hyphens and underscores and lowercasing
     * (e.g. "half-orc", "Half_Orc", and "halforc" all resolve to {@code HALFORC}).
     * Falls back to {@link #HUMAN} if unrecognised.
     *
     * @param s the raw text representation of the race name
     * @return the matching constant, or {@code HUMAN} as the default
     */
    public static CharacterRace fromString(String s) {
        if (s == null) return HUMAN;
        String normalised = s.toLowerCase().replace("-", "").replace("_", "");
        for (CharacterRace r : values()) {
            if (r.id.equals(normalised)) return r;
        }
        return HUMAN;
    }
}
