package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class CharacterResources {

    private int health;
    private int mana;
    private int stamina;
    private int carryWeight;
}
