package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.character.NpcTrust;

import java.util.Optional;

/**
 * Data access repository for {@link NpcTrust} relationship records.
 *
 * @author t1tanic
 * @version 1.0
 */
public interface NpcTrustRepository extends JpaRepository<NpcTrust, Long> {

    /**
     * Returns the trust record for a specific player–NPC pair, if one exists.
     *
     * @param playerId the player's primary key
     * @param npcId    the NPC's primary key
     * @return the trust record, or empty if the player has not yet interacted with this NPC
     */
    Optional<NpcTrust> findByPlayerIdAndNpcId(Long playerId, Long npcId);
}
