package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterIdentity {

    private String race;
    private String characterClass;
    private String gender;
    private String background;
}
