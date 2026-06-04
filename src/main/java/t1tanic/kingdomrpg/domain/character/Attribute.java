package t1tanic.kingdomrpg.domain.character;

public enum Attribute {

    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA;

    /** Three-letter abbreviation used in UI display, e.g. STR, DEX. */
    public String abbrev() { return name().substring(0, 3); }

    /** Lowercase key matching JSON payload keys sent by the frontend. */
    public String key() {
        return name().toLowerCase();
    }

    public static Attribute fromKey(String key) {
        return valueOf(key.toUpperCase());
    }
}
