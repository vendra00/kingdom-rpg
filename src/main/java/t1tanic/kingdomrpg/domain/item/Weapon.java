package t1tanic.kingdomrpg.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("WEAPON")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Weapon extends Item {

    private String damageDice;   // e.g. "1d6"
    private String damageType;   // e.g. "slashing"

    @Override public ItemTag getItemTag()    { return ItemTag.EQUIPMENT; }
    @Override public String  getTypeLabel()  { return "Weapon"; }
}
