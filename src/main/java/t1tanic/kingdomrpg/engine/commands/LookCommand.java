package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LookCommand implements Command {

    private final ItemRepository itemRepository;

    @Override
    public String execute(Player player, String[] args) {
        Room room = player.getCurrentRoom();
        StringBuilder sb = new StringBuilder();
        sb.append("=== [room]").append(room.getName()).append("[/room] ===\n");
        sb.append("[narrate]").append(room.getDescription()).append("[/narrate]").append("\n\n");

        List<String> exits = new ArrayList<>();
        if (room.getNorthId() != null) exits.add("[exit]north[/exit]");
        if (room.getSouthId() != null) exits.add("[exit]south[/exit]");
        if (room.getEastId() != null) exits.add("[exit]east[/exit]");
        if (room.getWestId() != null) exits.add("[exit]west[/exit]");
        sb.append("Exits: ").append(exits.isEmpty() ? "none" : String.join(", ", exits)).append("\n");

        List<Item> items = itemRepository.findByRoomId(room.getId());
        if (!items.isEmpty()) {
            sb.append("Items (use 'take <name>'): ").append(
                items.stream()
                    .map(item -> "[item]" + item.getName() + "[/item]")
                    .collect(Collectors.joining(", "))
            );
        }

        return sb.toString();
    }
}
