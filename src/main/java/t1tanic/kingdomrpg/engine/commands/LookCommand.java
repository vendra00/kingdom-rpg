package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.engine.MarkupTag;
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
        sb.append("=== ").append(MarkupTag.ROOM.wrap(room.getName())).append(" ===\n");
        sb.append(MarkupTag.NARRATE.wrap(room.getDescription())).append("\n\n");

        List<String> exits = new ArrayList<>();
        if (room.getNorthId() != null) exits.add(MarkupTag.EXIT.wrap("north"));
        if (room.getSouthId() != null) exits.add(MarkupTag.EXIT.wrap("south"));
        if (room.getEastId()  != null) exits.add(MarkupTag.EXIT.wrap("east"));
        if (room.getWestId()  != null) exits.add(MarkupTag.EXIT.wrap("west"));
        sb.append("Exits: ").append(exits.isEmpty() ? "none" : String.join(", ", exits)).append("\n");

        List<Item> items = itemRepository.findByRoomId(room.getId());
        if (!items.isEmpty()) {
            sb.append("Items (use 'take <name>'): ").append(
                items.stream()
                    .map(item -> MarkupTag.ITEM.wrap(item.getName()))
                    .collect(Collectors.joining(", "))
            );
        }

        return sb.toString();
    }
}
