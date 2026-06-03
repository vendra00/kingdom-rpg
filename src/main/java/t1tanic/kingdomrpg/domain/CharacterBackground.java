package t1tanic.kingdomrpg.domain;

import java.util.Map;

import static t1tanic.kingdomrpg.domain.Attribute.*;

public enum CharacterBackground {

    NOBLE    (Map.of(CHARISMA,    1)),
    SOLDIER  (Map.of(STRENGTH,    1)),
    SCHOLAR  (Map.of(INTELLIGENCE, 1)),
    OUTLANDER(Map.of(CONSTITUTION, 1)),
    CRIMINAL (Map.of(DEXTERITY,   1)),
    ACOLYTE  (Map.of(WISDOM,      1));

    private final Map<Attribute, Integer> bonuses;

    CharacterBackground(Map<Attribute, Integer> b) { this.bonuses = b; }

    public Map<Attribute, Integer> getBonuses() { return bonuses; }

    public static CharacterBackground fromString(String s) {
        if (s == null) return NOBLE;
        try { return valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException e) { return NOBLE; }
    }
}
