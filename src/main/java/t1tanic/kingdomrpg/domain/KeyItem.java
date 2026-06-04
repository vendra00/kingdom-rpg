package t1tanic.kingdomrpg.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("KEY_ITEM")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class KeyItem extends Item {

    @Override public ItemTag getItemTag()   { return ItemTag.KEY_ITEM; }
    @Override public String  getTypeLabel() { return "Key Item"; }
}
