package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.item.Item;

import java.util.List;

/**
 * Data access repository layer interface handling persistent management for {@link Item} assets.
 * <p>Extends {@link JpaRepository} capabilities to leverage Spring Data JPA, providing
 * object-relational mapping operations for the polymorphic item inheritance hierarchy.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Retrieves all items currently dropped on the floor of a specific world environment location.
     *
     * @param roomId the unique database identifier of the target room container
     * @return a {@link List} containing all matching {@link Item} records currently situated in the room
     */
    List<Item> findByRoomId(Long roomId);
    /**
     * Retrieves items in a room filtered by their current visibility state.
     *
     * @param roomId  the unique database identifier of the target room
     * @param visible {@code true} to return only visible items; {@code false} for hidden ones
     * @return a {@link List} of matching {@link Item} records
     */
    List<Item> findByRoomIdAndVisible(Long roomId, boolean visible);
    /**
     * Retrieves all items currently held inside a specific character's inventory container.
     *
     * @param playerId the unique database identifier of the target player character holding the items
     * @return a {@link List} containing all matching {@link Item} records carried by the player
     */
    List<Item> findByPlayerId(Long playerId);
}
