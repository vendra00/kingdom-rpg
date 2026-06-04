package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a hand-held defensive equipment item within the RPG domain.
 * <p>Shields are off-hand tactical items that provide an additive bonus to a character's
 * total Armor Class (AC). This class extends {@link Item} and integrates into the shared
 * joined inheritance hierarchy using a specialized discriminator value strategy.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@DiscriminatorValue("SHIELD")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Shield extends Item {
    /**
     * The numeric defensive rating increase added to the character's core Armor Class
     * while this shield is actively equipped.
     * <p>Typically configured to a baseline value of {@code +2} following standard d20/D&D 5e mechanics.</p>
     */
    private int defenseBonus;
    /**
     * {@inheritDoc}
     *
     * @return the definitive classification tag {@link ItemTag#EQUIPMENT}
     */
    @Override
    public ItemTag getItemTag() {
        return ItemTag.EQUIPMENT;
    }
    /**
     * {@inheritDoc}
     *
     * @return a static display text {@code "Shield"} label indicating the item subcategory
     */
    @Override
    public String getTypeLabel() {
        return "Shield";
    }
}