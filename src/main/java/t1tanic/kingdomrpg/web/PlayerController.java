package t1tanic.kingdomrpg.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST controller handling client queries and administration operations related to player data.
 * <p>Exposes HTTP endpoints under the {@code /api} path mapping. This layer fetches persistent entity data
 * from the data layer via {@link PlayerRepository} and projects it safely into lightweight, read-only
 * Data Transfer Objects (DTOs) suitable for network payload transport.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerRepository playerRepository;

    /**
     * Retrieves a summarized directory of all player characters, ordered by their recent update activity.
     * <p>Transforms underlying domain entities into clean structural JSON output maps via {@link PlayerSummaryDto}.
     * Includes null-safe protection handling across optional embedded values and transient timestamp fields.</p>
     *
     * @return a {@link List} containing high-level summary snapshots of all existing character records
     */
    @GetMapping("/players")
    public List<PlayerSummaryDto> listPlayers() {
        return playerRepository.findAllByOrderByUpdatedAtDesc().stream()
            .map(p -> new PlayerSummaryDto(
                p.getName(),
                p.getIdentity() != null ? p.getIdentity().getRace().displayName() : null,
                p.getIdentity() != null ? p.getIdentity().getCharacterClass().displayName() : null,
                p.getUpdatedAt() != null
                    ? p.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : null
            ))
            .toList();
    }
}
