package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.character.Npc;

import java.util.List;

/**
 * Data access repository for {@link Npc} entities.
 *
 * @author t1tanic
 * @version 1.0
 */
public interface NpcRepository extends JpaRepository<Npc, Long> {

    /**
     * Returns all NPCs whose current location matches the given room.
     *
     * @param roomId the primary key of the target room
     * @return NPCs present in that room, in insertion order
     */
    List<Npc> findByCurrentRoomId(Long roomId);

    /**
     * Returns only the visible NPCs in the given room — used by look, talk, and persuade
     * so that hidden NPCs are not revealed until the player discovers them.
     *
     * @param roomId the primary key of the target room
     * @return visible NPCs present in that room
     */
    List<Npc> findByCurrentRoomIdAndVisibleTrue(Long roomId);
}
