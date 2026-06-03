package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TakeCommand implements Command {

    private final ItemRepository itemRepository;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) return "Take what?";

        String itemName = String.join(" ", args).toLowerCase();
        List<Item> roomItems = itemRepository.findByRoomId(player.getCurrentRoom().getId());

        return roomItems.stream()
            .filter(item -> item.getName().toLowerCase().contains(itemName))
            .findFirst()
            .map(item -> {
                item.setRoom(null);
                item.setPlayer(player);
                itemRepository.save(item);
                return "You pick up the " + item.getName() + ".";
            })
            .orElse("There's no " + itemName + " here.");
    }
}
