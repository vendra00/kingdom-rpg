package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.enums.DamageType;
import t1tanic.kingdomrpg.domain.item.enums.ItemTag;
import t1tanic.kingdomrpg.domain.item.enums.WeaponRange;

/**
 * Represents an offensive combat equipment item within the RPG domain.
 * <p>Weapons define explicit minimum and maximum base attack values along with their engagement
 * range and damage classification. Effective attack bounds degrade proportionally with item
 * condition, reaching zero when the item is {@link t1tanic.kingdomrpg.domain.item.enums.ItemCondition#BROKEN}.</p>
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
     * The minimum base damage dealt by this weapon before condition scaling is applied.
     * <p>Example: {@code 2} for a short sword.</p>
     */
    private int attackMin;
    /**
     * The maximum base damage dealt by this weapon before condition scaling is applied.
     * <p>Example: {@code 5} for a short sword.</p>
     */
    private int attackMax;
    /**
     * The elemental or physical classification of the damage dealt by the weapon.
     * Interacts with creature resistances, immunities, or vulnerabilities.
     */
    @Enumerated(EnumType.STRING)
    private DamageType damageType;
    /**
     * The effective engagement reach category of this weapon.
     * Defaults to {@link WeaponRange#MELEE} for hand-to-hand weapons.
     */
    @Enumerated(EnumType.STRING)
    private WeaponRange weaponRange = WeaponRange.MELEE;

    /**
     * Returns the condition-scaled minimum attack value.
     * Returns {@code 0} if the item is broken, otherwise at least {@code 1}.
     */
    public int getEffectiveAttackMin() {
        if (isBroken()) return 0;
        return Math.max(1, (int) Math.round(attackMin * conditionMultiplier()));
    }

    /**
     * Returns the condition-scaled maximum attack value.
     * Returns {@code 0} if the item is broken, otherwise at least {@code 1}.
     */
    public int getEffectiveAttackMax() {
        if (isBroken()) return 0;
        return Math.max(1, (int) Math.round(attackMax * conditionMultiplier()));
    }

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
