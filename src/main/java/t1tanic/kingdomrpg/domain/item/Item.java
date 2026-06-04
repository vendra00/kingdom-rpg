package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import t1tanic.kingdomrpg.domain.BaseEntity;
import t1tanic.kingdomrpg.domain.character.Player;
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
