package t1tanic.kingdomrpg.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CONSUMABLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Consumable extends Item {

    private String effect;          // what happens when used
    private int    charges = 1;

    @Override public ItemTag getItemTag()   { return ItemTag.CONSUMABLE; }
    @Override public String  getTypeLabel() { return "Consumable"; }
}
