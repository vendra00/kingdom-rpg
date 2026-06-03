package t1tanic.kingdomrpg.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.repository.PlayerRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

@Service
@RequiredArgsConstructor
public class GameEngine {

    private final CommandParser commandParser;
    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public String processCommand(String playerName, String input) {
        Player player = playerRepository.findByName(playerName)
            .orElseGet(() -> createNewPlayer(playerName));
        return commandParser.parse(player, input);
    }

    private Player createNewPlayer(String name) {
        Room startRoom = roomRepository.findById(1L)
            .orElseThrow(() -> new IllegalStateException("Start room not found — world not seeded"));
        Player player = new Player();
        player.setName(name);
        player.setCurrentRoom(startRoom);
        // Fill resources to max based on starting attributes (all 10)
        player.setHealth(player.getMaxHealth());
        player.setMana(player.getMaxMana());
        player.setStamina(player.getMaxStamina());
        return playerRepository.save(player);
    }
}
