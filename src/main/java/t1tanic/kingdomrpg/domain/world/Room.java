package t1tanic.kingdomrpg.domain.world;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.BaseEntity;

/**
 * Domain model entity representing an environmental coordinate or spatial node within the game world grid.
 * <p>Extends {@link BaseEntity} to inherit basic identity auditing signatures. Each room object contains
 * dynamic narrative logs and maps absolute database foreign ID references across the four core compass vectors
 * to handle directional graph mapping topologies.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Room extends BaseEntity {
    /**
     * The unique, descriptive title of the geographical room or area.
     */
    private String name;
    /**
     * The detailed environmental narrative text mapped out to players upon looking or entering the room.
     */
    @Column(length = 1000)
    private String description;
    /**
     * The database primary identifier of the adjacent room located to the north, or {@code null} if blocked.
     */
    private Long northId;
    /**
     * The database primary identifier of the adjacent room located to the south, or {@code null} if blocked.
     */
    private Long southId;
    /**
     * The database primary identifier of the adjacent room located to the east, or {@code null} if blocked.
     */
    private Long eastId;
    /**
     * The database primary identifier of the adjacent room located to the west, or {@code null} if blocked.
     */
    private Long westId;
}