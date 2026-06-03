package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.repository.PlayerRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoCommand implements Command {

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final LookCommand lookCommand;

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

        player.setCurrentRoom(nextRoom);
        playerRepository.save(player);

        return "You move " + direction + ".\n\n" + lookCommand.execute(player, new String[]{});
    }
}
