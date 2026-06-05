package t1tanic.kingdomrpg.domain.magic.enums;

/**
 * Elemental and physical damage types dealt by cantrips and spells.
 * <p>{@link #label()} returns the lowercase string stored in the database and displayed in the UI.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum DamageType {
    FIRE      ("fire"),
    COLD      ("cold"),
    LIGHTNING ("lightning"),
    RADIANT   ("radiant"),
    FORCE     ("force"),
    NECROTIC  ("necrotic"),
    ACID      ("acid"),
    POISON    ("poison"),
    PIERCING  ("piercing"),
    PSYCHIC   ("psychic");

    private final String label;

    DamageType(String label) { this.label = label; }

    /** Lowercase string stored in the database and shown in the UI (e.g. "fire", "necrotic"). */
    public String label() { return label; }
}
