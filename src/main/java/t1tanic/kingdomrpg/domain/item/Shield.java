package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SHIELD")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Shield extends Item {

    private int defenseBonus;   // typically +2 in D&D 5e

    @Override public ItemTag getItemTag()   { return ItemTag.EQUIPMENT; }
    @Override public String  getTypeLabel() { return "Shield"; }
}
