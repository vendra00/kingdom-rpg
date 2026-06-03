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

    public int get(Attribute attr) {
        return switch (attr) {
            case STRENGTH     -> strength;
            case DEXTERITY    -> dexterity;
            case CONSTITUTION -> constitution;
            case INTELLIGENCE -> intelligence;
            case WISDOM       -> wisdom;
            case CHARISMA     -> charisma;
        };
    }

    public int modifier(Attribute attr) {
        return modifier(get(attr));
    }

    // D&D modifier: floor((value − 10) / 2)
    public static int modifier(int value) {
        return (int) Math.floor((value - 10) / 2.0);
    }
}
