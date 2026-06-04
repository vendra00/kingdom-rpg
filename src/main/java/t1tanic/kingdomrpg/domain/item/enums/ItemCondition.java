package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Represents the physical wear state of an item based on its remaining durability percentage.
 * <p>Condition degrades as durability drops and applies a scaling multiplier to item performance stats.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum ItemCondition {
    PRISTINE("Pristine", 1.00),
    GOOD    ("Good",     0.90),
    WORN    ("Worn",     0.75),
    DAMAGED ("Damaged",  0.50),
    BROKEN  ("Broken",   0.00);

    private final String label;
    private final double multiplier;

    ItemCondition(String label, double multiplier) {
        this.label      = label;
        this.multiplier = multiplier;
    }

    public String label()      { return label; }
    public double multiplier() { return multiplier; }

    /**
     * Derives condition from a durability ratio.
     *
     * @param durability    current durability value
     * @param maxDurability maximum possible durability
     * @return the matching {@link ItemCondition} tier
     */
    public static ItemCondition of(int durability, int maxDurability) {
        if (maxDurability <= 0 || durability <= 0) return BROKEN;
        double pct = (double) durability / maxDurability;
        if (pct >= 1.00) return PRISTINE;
        if (pct >= 0.75) return GOOD;
        if (pct >= 0.50) return WORN;
        if (pct >  0.00) return DAMAGED;
        return BROKEN;
    }
}
