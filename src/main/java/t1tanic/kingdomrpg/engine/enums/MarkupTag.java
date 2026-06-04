package t1tanic.kingdomrpg.engine.enums;

/**
 * Defines semantic custom formatting tags utilized to wrap game text log outputs.
 * <p>These markup descriptors are processed by frontend presentation components or console rendering
 * engines to apply contextual style properties, color palettes, or interactive hover behaviors based
 * on the type of game element (such as distinguishing a room name from an acquirable item).</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum MarkupTag {
    /**
     * Used for formatting environmental geographic room titles or unique region locations.
     */
    ROOM("room"),
    /**
     * Used for structural portal pathways or directional exit nodes (e.g., "north", "iron gate").
     */
    EXIT("exit"),
    /**
     * Used for loot assets, weapons, equipment drops, or general consumable item tokens.
     */
    ITEM("item"),
    /**
     * Used for atmospheric narrative flavor, sensory logs, or overarching story exposition blocks.
     */
    NARRATE("narrate");

    private final String tag;

    MarkupTag(String tag) {
        this.tag = tag;
    }
    /**
     * Encloses raw text inside corresponding opening and closing bbcode-style markup brackets.
     * <p>Example: {@code MarkupTag.ITEM.wrap("Rusty Sword")} yields {@code "[item]Rusty Sword[/item]"}.</p>
     *
     * @param text the raw string content requiring cosmetic encapsulation
     * @return the newly aggregated markup string layout
     */
    public String wrap(String text) {
        return "[" + tag + "]" + text + "[/" + tag + "]";
    }

    /**
     * Wraps text in an inline color tag rendered by the frontend as a CSS {@code style="color"} span.
     * <p>Example: {@code MarkupTag.color("#ff4500", "Fire")} yields {@code "[c=#ff4500]Fire[/c]"}.</p>
     *
     * @param cssColor any valid CSS color value (hex, rgb, named)
     * @param text     the content to colorize
     * @return the color-wrapped markup string
     */
    public static String color(String cssColor, String text) {
        return "[c=" + cssColor + "]" + text + "[/c]";
    }
}
