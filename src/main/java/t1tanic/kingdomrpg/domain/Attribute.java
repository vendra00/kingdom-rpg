package t1tanic.kingdomrpg.domain;

public enum Attribute {

    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA;

    /** Lowercase key matching JSON payload keys sent by the frontend. */
    public String key() {
        return name().toLowerCase();
    }

    public static Attribute fromKey(String key) {
        return valueOf(key.toUpperCase());
    }
}
