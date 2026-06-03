package t1tanic.kingdomrpg.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cantrip {

    @Id
    private String id;

    private String name;
    private String school;

    @Column(length = 500)
    private String description;

    private String allowedClasses; // comma-separated: "mage,rogue"
    private String damageType;     // null if non-damage

    @Enumerated(EnumType.STRING)
    private CantripEffect effect;
}
