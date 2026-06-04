package t1tanic.kingdomrpg.domain.character.enums;

import java.util.Map;

import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Defines the playable character races in the RPG system and their respective starting attribute bonuses.
 * <p>Each race maps to an immutable set of attribute modifications applied during character generation
 * or statistical calculations. It includes resilient parsing utility strategies to resolve user input or
 * database strings cleanly back into the core constants.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CharacterRace {

    /**
     * Versatile and well-rounded. Receives a minor +1 bonus to all six core attributes.
     */
    HUMAN   (Map.of(STRENGTH,1, DEXTERITY,1, CONSTITUTION,1, INTELLIGENCE,1, WISDOM,1, CHARISMA,1)),

    /**
     * Graceful and intellectually sharp. Receives +2 Dexterity and +1 Intelligence.
     */
    ELF     (Map.of(DEXTERITY,2, INTELLIGENCE,1)),

    /**
     * Sturdy and physically powerful. Receives +2 Constitution and +1 Strength.
     */
    DWARF   (Map.of(CONSTITUTION,2, STRENGTH,1)),

    /**
     * Exceptionally strong and resilient. Receives +2 Strength and +1 Constitution.
     */
    HALFORC (Map.of(STRENGTH,2, CONSTITUTION,1)),

    /**
     * Nimble and inherently expressive. Receives +2 Dexterity and +1 Charisma.
     */
    HALFLING(Map.of(DEXTERITY,2, CHARISMA,1)),

    /**
     * Alluring, cunning, and otherworldly. Receives +2 Charisma and +1 Intelligence.
     */
    TIEFLING(Map.of(CHARISMA,2, INTELLIGENCE,1));

    private final Map<Attribute, Integer> bonuses;

    CharacterRace(Map<Attribute, Integer> b) { this.bonuses = b; }

    /**
     * Retrieves the map of attribute type keys to their corresponding integer bonus modifiers.
     *
     * @return an unmodifiable {@link Map} containing the structural attribute adjustments for this race
     */
    public Map<Attribute, Integer> getBonuses() { return bonuses; }

    /**
     * Safely parses a string value into its corresponding {@code CharacterRace} constant.
     * <p>This strategy normalizes input by converting to lowercase and stripping out special symbols
     * like hyphens and underscores (e.g., "half-orc" and "Half_Orc" both successfully map to {@code HALFORC}).
     * If the string is null or unrecognized, it falls back gracefully to a sensible default.</p>
     *
     * @param s the raw text representation of the race name to parse
     * @return the matching {@code CharacterRace} constant, or {@code HUMAN} as the safety fallback choice
     */
    public static CharacterRace fromString(String s) {
        if (s == null) return HUMAN;
        return switch (s.toLowerCase().replace("-", "").replace("_", "")) {
            case "elf"      -> ELF;
            case "dwarf"    -> DWARF;
            case "halforc"  -> HALFORC;
            case "halfling" -> HALFLING;
            case "tiefling" -> TIEFLING;
            default         -> HUMAN;
        };
    }
}
