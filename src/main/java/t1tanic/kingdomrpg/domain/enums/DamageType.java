package t1tanic.kingdomrpg.domain.enums;

/**
 * Elemental and physical damage types used across weapons and spells.
 * <p>{@link #label()} is the capitalised display name; {@link #cssColor()} drives UI colour coding.</p>
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

    /** Capitalised display name shown in the UI (e.g. "Fire", "Necrotic"). */
    public String label()    { return label; }

    /** Hex colour used for UI markup (e.g. {@code "#ff4500"}). */
    public String cssColor() { return cssColor; }
}
