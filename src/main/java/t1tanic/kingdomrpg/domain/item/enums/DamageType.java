package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Classifies the elemental or physical nature of damage a weapon deals.
 * <p>Used for resistance, immunity, and vulnerability interactions in combat resolution.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum DamageType {
    SLASHING    ("Slashing",     "#c0c0c0"),
    PIERCING    ("Piercing",     "#e8e8e8"),
    BLUDGEONING ("Bludgeoning",  "#cd7f32"),
    FIRE        ("Fire",         "#ff4500"),
    COLD        ("Cold",         "#00bfff"),
    LIGHTNING   ("Lightning",    "#ffe066"),
    POISON      ("Poison",       "#39d353"),
    ACID        ("Acid",         "#adff2f"),
    NECROTIC    ("Necrotic",     "#9b59b6"),
    RADIANT     ("Radiant",      "#ffd700"),
    THUNDER     ("Thunder",      "#7ec8e3"),
    PSYCHIC     ("Psychic",      "#ff69b4"),
    FORCE       ("Force",        "#00ffff");

    private final String label;
    private final String cssColor;

    DamageType(String label, String cssColor) {
        this.label    = label;
        this.cssColor = cssColor;
    }

    public String label()    { return label; }
    public String cssColor() { return cssColor; }
}
