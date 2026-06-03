package t1tanic.kingdomrpg.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterAttributes {

    private int strength     = 10;
    private int dexterity    = 10;
    private int constitution = 10;
    private int intelligence = 10;
    private int wisdom       = 10;
    private int charisma     = 10;

    // D&D modifier: floor((value − 10) / 2)
    public static int modifier(int value) {
        return (int) Math.floor((value - 10) / 2.0);
    }
}
