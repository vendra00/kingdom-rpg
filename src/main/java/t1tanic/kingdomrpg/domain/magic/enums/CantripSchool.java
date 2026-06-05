package t1tanic.kingdomrpg.domain.magic.enums;

/**
 * Represents the eight recognised schools of arcane and divine magic.
 * <p>Each constant carries the canonical display name used in the UI and stored in the database.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CantripSchool {
    EVOCATION    ("Evocation"),
    NECROMANCY   ("Necromancy"),
    CONJURATION  ("Conjuration"),
    TRANSMUTATION("Transmutation"),
    ENCHANTMENT  ("Enchantment"),
    ILLUSION     ("Illusion"),
    DIVINATION   ("Divination"),
    ABJURATION   ("Abjuration");

    private final String displayName;

    CantripSchool(String displayName) { this.displayName = displayName; }

    public String displayName() { return displayName; }
}
