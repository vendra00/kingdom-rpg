package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Represents the physical wear state of an item based on its remaining durability percentage.
 * <p>Condition degrades as durability drops and applies a scaling multiplier to item performance stats.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum ItemCondition {
    PRISTINE("Pristine", 1.00, "#00ff41"),
    GOOD    ("Good",     0.90, "#7fff00"),
    WORN    ("Worn",     0.75, "#ffd700"),
    DAMAGED ("Damaged",  0.50, "#ff8c00"),
    BROKEN  ("Broken",   0.00, "#ff3333");

    private final String label;
    private final double multiplier;
    private final String cssColor;

    ItemCondition(String label, double multiplier, String cssColor) {
        this.label      = label;
        this.multiplier = multiplier;
        this.cssColor   = cssColor;
    }

    public String label()      { return label; }
    public double multiplier() { return multiplier; }
    public String cssColor()   { return cssColor; }

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
