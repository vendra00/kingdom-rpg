package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.item.enums.ItemTag;

/**
 * Represents a wearable defensive gear item within the RPG domain.
 * <p>Armor items modify a character's physical resilience by establishing a baseline
 * Armor Class (AC) value. This class extends {@link Item} and integrates into the shared
 * joined inheritance hierarchy using a specialized discriminator value strategy.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@DiscriminatorValue("ARMOR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Armor extends Item {
    /**
     * The baseline defensive rating calculation score provided by this armor configuration.
     * <p>Example: {@code 11} for standard leather armor, {@code 18} for full plate gear.</p>
     */
    private int armorClass;
    /**
     * The structural weight classification tier designation of the armor.
     * Dictates proficiency prerequisites and potential agility or noise stealth penalties.
     * <p>Expected values: {@code "Light"}, {@code "Medium"}, {@code "Heavy"}.</p>
     */
    private String armorType;
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
     * @return a static display text {@code "Armor"} label indicating the item subcategory
     */
    @Override
    public String getTypeLabel() {
        return "Armor";
    }
}