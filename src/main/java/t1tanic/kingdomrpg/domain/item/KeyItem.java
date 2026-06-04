package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represents a specialized quest, story progression, or utility unlock item within the RPG domain.
 * <p>Key items are critical assets (such as rusty vault keys, royal seals, or map fragments)
 * that bypass standard disposal, selling, or trade loops. This class extends {@link Item} and
 * maps to its own entry using a discriminator value strategy inside the joined inheritance structure.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@DiscriminatorValue("KEY_ITEM")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class KeyItem extends Item {
    /**
     * {@inheritDoc}
     *
     * @return the definitive classification tag {@link ItemTag#KEY_ITEM}
     */
    @Override public ItemTag getItemTag()   { return ItemTag.KEY_ITEM; }
    /**
     * {@inheritDoc}
     *
     * @return a static display text {@code "Key Item"} label indicating the item subcategory
     */
    @Override public String  getTypeLabel() { return "Key Item"; }
}