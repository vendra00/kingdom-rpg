package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Defines high-level categorization tags used to organize and filter items within inventories or stores.
 * <p>These structural classification indexes group shared behaviors, helping game logic distinguish
 * between usable items, gear options, vital quest items, or raw trade goods.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum ItemTag {
    /**
     * Items intended to be eaten, drunk, or applied, usually consumed entirely on use (e.g., Potions, Food).
     */
    CONSUMABLE("Consumable"),
    /**
     * Wearable or holdable gear that alters statistical properties (e.g., Weapons, Armor, Rings).
     */
    EQUIPMENT ("Equipment"),
    /**
     * Critical story-progression items that cannot be easily discarded, sold, or lost (e.g., Keys, Quest items).
     */
    KEY_ITEM  ("Key Item"),
    /**
     * Raw processing materials used in crafting or upgrade mechanics (e.g., Ores, Herbs, Cloth).
     */
    MATERIAL  ("Material"),
    /**
     * General miscellaneous items, trinkets, or junk lacking standalone mechanical classifications (e.g., Vendor loot).
     */
    MISC      ("Misc");

    private final String label;

    ItemTag(String label) { this.label = label; }
    /**
     * @return the human-readable display string used for categorizing items in frontend inventory menus
     */
    public String label() { return label; }
}
