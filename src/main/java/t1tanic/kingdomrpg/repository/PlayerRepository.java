package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.character.Player;

import java.util.List;
import java.util.Optional;

/**
 * Data access repository layer interface handling persistent management for {@link Player} characters.
 * <p>Extends {@link JpaRepository} capabilities to leverage Spring Data JPA, providing lifecycle
 * management and lookup strategies against user-controlled character graphs.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public interface PlayerRepository extends JpaRepository<Player, Long> {
    /**
     * Resolves a player character graph by searching for an exact match against their unique name identifier.
     *
     * @param name the unique name or account identity handle to look up
     * @return an {@link Optional} containing the associated {@link Player} entity if found, or {@link Optional#empty()}
     */
    Optional<Player> findByName(String name);
    /**
     * Retrieves all tracked player records sorted by their last modification timestamp in descending order.
     * <p>Typically utilized by administration dashboards, active session indexes, or leaderboard components
     * to pull a list of recently active characters first.</p>
     *
     * @return a {@link List} containing all {@link Player} entities sorted from newest modification to oldest
     */
    List<Player> findAllByOrderByUpdatedAtDesc();
}
