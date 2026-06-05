package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.character.NpcConversation;

import java.util.List;

/**
 * Data access repository for persisted NPC dialogue history.
 *
 * @author t1tanic
 * @version 1.0
 */
public interface NpcConversationRepository extends JpaRepository<NpcConversation, Long> {

    /**
     * Returns all messages in a player–NPC dialogue, oldest first.
     *
     * @param playerId the player's primary key
     * @param npcId    the NPC's primary key
     * @return ordered list of conversation turns
     */
    List<NpcConversation> findByPlayerIdAndNpcIdOrderByCreatedAtAsc(Long playerId, Long npcId);
}
