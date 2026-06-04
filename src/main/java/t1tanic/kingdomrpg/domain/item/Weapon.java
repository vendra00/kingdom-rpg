package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents an offensive combat equipment item within the RPG domain.
 * <p>Weapon items hold the baseline parameters required to resolve combat attack vectors,
 * housing formulas for raw damage generation and damage classifications. This class extends
 * {@link Item} and integrates into the shared joined inheritance hierarchy using a specialized
 * discriminator value strategy.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@DiscriminatorValue("WEAPON")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Weapon extends Item {
    /**
     * The dice notation calculation formula dictating the base damage roll calculation.
     * <p>Example: {@code "1d6"} for a shortsword, {@code "2d6"} for a greatsword.</p>
     */
    private String damageDice;
    /**
     * The structural type classification of the damage dealt by the weapon.
     * Interacts with creature resistances, immunities, or vulnerabilities.
     * <p>Example: {@code "slashing"}, {@code "piercing"}, {@code "bludgeoning"}.</p>
     */
    private String damageType;
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
     * @return a static display text {@code "Weapon"} label indicating the item subcategory
     */
    @Override
    public String getTypeLabel() {
        return "Weapon";
    }
}