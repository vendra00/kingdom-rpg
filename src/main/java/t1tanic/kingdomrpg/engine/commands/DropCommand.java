package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DropCommand implements Command {

    private final ItemRepository itemRepository;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) return "Drop what?";

        String itemName = String.join(" ", args).toLowerCase();
        List<Item> inventory = itemRepository.findByPlayerId(player.getId());

        return inventory.stream()
            .filter(item -> item.getName().toLowerCase().contains(itemName))
            .findFirst()
            .map(item -> {
                item.setPlayer(null);
                item.setRoom(player.getCurrentRoom());
                itemRepository.save(item);
                return "You drop the " + item.getName() + ".";
            })
            .orElse("You don't have a " + itemName + ".");
    }
}
