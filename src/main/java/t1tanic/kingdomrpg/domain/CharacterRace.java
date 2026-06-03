package t1tanic.kingdomrpg.domain;

import java.util.Map;

import static t1tanic.kingdomrpg.domain.Attribute.*;

public enum CharacterRace {

    HUMAN   (Map.of(STRENGTH,1, DEXTERITY,1, CONSTITUTION,1, INTELLIGENCE,1, WISDOM,1, CHARISMA,1)),
    ELF     (Map.of(DEXTERITY,2, INTELLIGENCE,1)),
    DWARF   (Map.of(CONSTITUTION,2, STRENGTH,1)),
    HALFORC (Map.of(STRENGTH,2, CONSTITUTION,1)),
    HALFLING(Map.of(DEXTERITY,2, CHARISMA,1)),
    TIEFLING(Map.of(CHARISMA,2, INTELLIGENCE,1));

    private final Map<Attribute, Integer> bonuses;

    CharacterRace(Map<Attribute, Integer> b) { this.bonuses = b; }

    public Map<Attribute, Integer> getBonuses() { return bonuses; }

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
