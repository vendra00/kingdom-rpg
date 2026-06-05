package t1tanic.kingdomrpg.domain.character.enums;

/**
 * Defines the relationship disposition of an NPC toward the player.
 * <p>Used both for behavioral logic (e.g., whether a {@code talk} command yields a response)
 * and for color-coded rendering in room descriptions.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum NpcFaction {
    FRIENDLY("#00ff41"),
    NEUTRAL ("#aaaaaa"),
    HOSTILE ("#ff4444");

    private final String cssColor;

    NpcFaction(String cssColor) {
        this.cssColor = cssColor;
    }

    /** CSS color value used to tint the NPC's name in game output. */
    public String cssColor() { return cssColor; }

    /** Capitalized display label (e.g., {@code "Friendly"}). */
    public String label() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
