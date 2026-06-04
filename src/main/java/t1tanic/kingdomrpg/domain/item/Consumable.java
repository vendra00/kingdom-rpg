package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a usable, limited-use item within the RPG domain.
 * <p>Consumables encapsulate items like potions, scrolls, rations, or elixirs that trigger a
 * specific gameplay effect upon use and have a limited tracking pool of charges before depletion.
 * This class extends {@link Item} and maps using a discriminator value strategy inside the joined
 * inheritance structure.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@DiscriminatorValue("CONSUMABLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Consumable extends Item {
    /**
     * The mechanical or narrative trigger string defining what status change or resource
     * modification occurs when the item is consumed (e.g., "HEAL_HP_20", "RESTORE_MANA").
     */
    private String effect;
    /**
     * The number of available activations remaining for this item before it is completely depleted.
     * Defaults to 1 for standard single-use items.
     */
    private int charges = 1;
    /**
     * {@inheritDoc}
     *
     * @return the definitive classification tag {@link ItemTag#CONSUMABLE}
     */
    @Override
    public ItemTag getItemTag() {
        return ItemTag.CONSUMABLE;
    }
    /**
     * {@inheritDoc}
     *
     * @return a static display text {@code "Consumable"} label indicating the item subcategory
     */
    @Override
    public String getTypeLabel() {
        return "Consumable";
    }
}