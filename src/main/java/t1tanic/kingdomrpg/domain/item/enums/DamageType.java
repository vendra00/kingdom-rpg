package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Classifies the elemental or physical nature of damage a weapon deals.
 * <p>Used for resistance, immunity, and vulnerability interactions in combat resolution.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum DamageType {
    SLASHING    ("Slashing"),
    PIERCING    ("Piercing"),
    BLUDGEONING ("Bludgeoning"),
    FIRE        ("Fire"),
    COLD        ("Cold"),
    LIGHTNING   ("Lightning"),
    POISON      ("Poison"),
    ACID        ("Acid"),
    NECROTIC    ("Necrotic"),
    RADIANT     ("Radiant"),
    THUNDER     ("Thunder"),
    PSYCHIC     ("Psychic"),
    FORCE       ("Force");

    private final String label;

    DamageType(String label) { this.label = label; }

    public String label() { return label; }
}
