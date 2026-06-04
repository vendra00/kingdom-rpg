package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.world.Room;

/**
 * Data access repository layer interface handling persistent management for {@link Room} nodes.
 * <p>Extends standard {@link JpaRepository} capabilities to offer built-in CRUD operations and
 * database structural bindings to map the spatial framework layout of the game world.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
}
