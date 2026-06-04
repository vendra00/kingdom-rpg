package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.PlayerRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.List;

/**
 * Command implementation responsible for transitioning player coordinates across spatial directions.
 * <p>This command evaluates compass vectors, matches structural doorway thresholds attached to the
 * current geographic layout, adjusts database references to shift coordinates, and automatically triggers an
 * environmental scene description logic payload to map out the new discovery zone.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoCommand implements Command {

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final LookCommand lookCommand;

    /**
     * {@inheritDoc}
     * <p>Inspects input tokens to determine targeted geographical directions. Validates adjacent index pathways,
     * handles structural boundaries or missing links gracefully, saves state updates to persistence layers,
     * and cascades execution directly into a descriptive view rendering step.</p>
     *
     * @param player the active player character attempting movement
     * @param args   argument tokens where the initial string segment indicates the heading (e.g., "north" or "n")
     * @return a narrative sequence indicating structural travel confirmations or boundary failure logs
     */
    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Go where? (north, south, east, west)";
        }

        String direction = args[0].toLowerCase();
        Room current = player.getCurrentRoom();
        Long nextRoomId = switch (direction) {
            case "north", "n" -> current.getNorthId();
            case "south", "s" -> current.getSouthId();
            case "east",  "e" -> current.getEastId();
            case "west",  "w" -> current.getWestId();
            default -> null;
        };

        if (nextRoomId == null && !List.of("north","south","east","west","n","s","e","w").contains(direction)) {
            return "Unknown direction: " + direction;
        }
        if (nextRoomId == null) {
            return "You can't go " + direction + " from here.";
        }

        Room nextRoom = roomRepository.findById(nextRoomId).orElse(null);
        if (nextRoom == null) {
            return "That path leads nowhere.";
        }

        log.debug("Moving '{}' → '{}'", current.getName(), nextRoom.getName());
        player.setCurrentRoom(nextRoom);
        playerRepository.save(player);

        return "You move " + direction + ".\n\n" + lookCommand.execute(player, new String[]{});
    }
}