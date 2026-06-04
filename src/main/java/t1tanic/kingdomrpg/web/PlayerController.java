package t1tanic.kingdomrpg.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerRepository playerRepository;

    @GetMapping("/players")
    public List<PlayerSummaryDto> listPlayers() {
        return playerRepository.findAllByOrderByUpdatedAtDesc().stream()
            .map(p -> new PlayerSummaryDto(
                p.getName(),
                p.getIdentity() != null ? p.getIdentity().getRace()           : null,
                p.getIdentity() != null ? p.getIdentity().getCharacterClass()  : null,
                p.getUpdatedAt() != null
                    ? p.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    : null
            ))
            .toList();
    }
}
