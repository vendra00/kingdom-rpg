package t1tanic.kingdomrpg.domain;

import java.util.Map;

public enum CharacterBackground {
    NOBLE    (Map.of("charisma",    1)),
    SOLDIER  (Map.of("strength",    1)),
    SCHOLAR  (Map.of("intelligence",1)),
    OUTLANDER(Map.of("constitution",1)),
    CRIMINAL (Map.of("dexterity",   1)),
    ACOLYTE  (Map.of("wisdom",      1));

    private final Map<String, Integer> bonuses;
    CharacterBackground(Map<String, Integer> b) { this.bonuses = b; }
    public Map<String, Integer> getBonuses() { return bonuses; }

    public static CharacterBackground fromString(String s) {
        if (s == null) return NOBLE;
        try { return valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException e) { return NOBLE; }
    }
}
