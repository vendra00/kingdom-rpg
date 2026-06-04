package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import t1tanic.kingdomrpg.domain.BaseEntity;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.enums.ItemCondition;
import t1tanic.kingdomrpg.domain.item.enums.ItemTag;
import t1tanic.kingdomrpg.domain.world.Room;

/**
 * Abstract base class for all items within the RPG world domain.
 * <p>This entity utilizes a {@link InheritanceType#JOINED} strategy to allow specialized
 * subclasses (such as weapons, armor, or consumables) to retain their own distinct database
 * tables while inheriting shared tracking state properties. An item exists mutually exclusively
 * either on the floor of a specific geographic location ({@link Room}) or within a character's
 * inventory ({@link Player}).</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "item_type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Item extends BaseEntity {
    /**
     * The human-readable name of the item used in game logs and inventory screens.
     */
    private String name;
    /**
     * A descriptive narrative details block outlining physical appearance or magical attributes.
     * Stored with an expanded length boundary to accommodate descriptive lore.
     */
    @Column(length = 500)
    private String description;
    /**
     * The physical weight mass of the item measured in grams.
     * Contributes directly to calculating inventory capacity limits on active characters.
     */
    private int weightGrams;
    /**
     * The specific world environment room location where this item is currently dropped.
     * Retains a {@code null} reference value if the item is held inside an inventory container.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;
    /**
     * The active player character currently carrying this item in their possession.
     * Retains a {@code null} reference value if the item is left lying on the ground inside a room.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;
    /**
     * Whether this item is currently visible to the player in its room.
     * Hidden items are concealed inside containers or obscured in the environment;
     * they become visible via {@code search} commands or a successful perception check.
     */
    private boolean visible = false;
    /**
     * Descriptive name of what conceals this item (e.g., {@code "a wooden chest"}, {@code "a hollow book"}).
     * {@code null} for free-standing visible items or items hidden without a named container.
     * When non-null, listed in the room as a visible object the player can search.
     */
    private String hiddenIn;
    /**
     * Minimum perception roll (d20 + WIS modifier) required to spot this item during a general scan.
     * A value of {@code 0} means the item reveals itself as soon as its container is directly searched.
     */
    private int perceptionDc = 0;

    /**
     * Current durability points remaining. Decreases via {@link #degrade(int)} as the item is used.
     * When it reaches zero the item is {@link ItemCondition#BROKEN} and provides no combat benefit.
     */
    private int durability    = 100;
    /**
     * Maximum possible durability for this item instance.
     * Acts as the denominator for condition percentage calculations.
     */
    private int maxDurability = 100;

    /**
     * Reduces durability by the given amount, clamping at zero.
     *
     * @param amount points of wear to apply
     */
    public void degrade(int amount) {
        durability = Math.max(0, durability - amount);
    }

    /**
     * Returns the current wear state derived from the durability ratio.
     *
     * @return the {@link ItemCondition} matching the current durability percentage
     */
    public ItemCondition getCondition() {
        return ItemCondition.of(durability, maxDurability);
    }

    /** @return {@code true} when durability has reached zero */
    public boolean isBroken() {
        return durability <= 0;
    }

    /**
     * Returns the performance scaling factor for the current condition (0.0 – 1.0).
     * Subclasses multiply their base stats by this value to compute effective stats.
     */
    public double conditionMultiplier() {
        return getCondition().multiplier();
    }

    /**
     * Retrieves the foundational classification category structural index tag for this item type.
     *
     * @return the relevant specific structural {@link ItemTag} configuration classification index
     */
    public abstract ItemTag getItemTag();
    /**
     * Returns a human-readable categorical metadata summary text string identifying the item subcategory.
     * <p>Examples: "Weapon", "Consumable", "Quest Item"</p>
     *
     * @return a display text {@link String} label designating the functional classification type
     */
    public abstract String getTypeLabel();
}
