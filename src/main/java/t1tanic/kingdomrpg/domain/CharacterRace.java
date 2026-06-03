package t1tanic.kingdomrpg.domain;

import java.util.Map;

public enum CharacterRace {
    HUMAN   (Map.of("strength",1,"dexterity",1,"constitution",1,"intelligence",1,"wisdom",1,"charisma",1)),
    ELF     (Map.of("dexterity",2,"intelligence",1)),
    DWARF   (Map.of("constitution",2,"strength",1)),
    HALFORC (Map.of("strength",2,"constitution",1)),
    HALFLING(Map.of("dexterity",2,"charisma",1)),
    TIEFLING(Map.of("charisma",2,"intelligence",1));

    private final Map<String, Integer> bonuses;
    CharacterRace(Map<String, Integer> b) { this.bonuses = b; }
    public Map<String, Integer> getBonuses() { return bonuses; }

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
