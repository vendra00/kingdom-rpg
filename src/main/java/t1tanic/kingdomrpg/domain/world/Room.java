package t1tanic.kingdomrpg.domain.world;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.BaseEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Room extends BaseEntity {

    private String name;

    @Column(length = 1000)
    private String description;

    private Long northId;
    private Long southId;
    private Long eastId;
    private Long westId;
}
