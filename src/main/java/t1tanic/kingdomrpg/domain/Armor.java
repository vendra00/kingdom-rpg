package t1tanic.kingdomrpg.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ARMOR")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Armor extends Item {

    private int    armorClass;               // base AC value, e.g. 11 for leather
    private String armorType;               // "Light", "Medium", "Heavy"

    @Override public ItemTag getItemTag()   { return ItemTag.EQUIPMENT; }
    @Override public String  getTypeLabel() { return "Armor"; }
}
