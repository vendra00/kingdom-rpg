package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
