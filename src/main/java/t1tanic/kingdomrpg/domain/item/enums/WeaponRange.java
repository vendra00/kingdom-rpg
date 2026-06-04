package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Classifies a weapon's effective operational engagement reach category.
 *
 * @author t1tanic
 * @version 1.0
 */
public enum WeaponRange {
    MELEE("Melee"),
    RANGED("Ranged");

    private final String label;

    WeaponRange(String label) { this.label = label; }

    public String label() { return label; }
}
