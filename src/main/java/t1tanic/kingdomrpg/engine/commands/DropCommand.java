package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DropCommand implements Command {

    private final ItemRepository   itemRepository;
    private final PlayerRepository playerRepository;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) return "Drop what?";

        String itemName = String.join(" ", args).toLowerCase();
        List<Item> inventory = itemRepository.findByPlayerId(player.getId());

        return inventory.stream()
            .filter(item -> item.getName().toLowerCase().contains(itemName))
            .findFirst()
            .map(item -> drop(player, item))
            .orElse("You don't have a " + itemName + ".");
    }

    private String drop(Player player, Item item) {
        log.debug("Dropped '{}' in '{}'", item.getName(), player.getCurrentRoom().getName());
        item.setPlayer(null);
        item.setRoom(player.getCurrentRoom());
        itemRepository.save(item);

        int newCarry = Math.max(0, player.getResources().getCarryWeight() - item.getWeightGrams());
        player.getResources().setCarryWeight(newCarry);
        playerRepository.save(player);

        return "You drop the %s (%.2f kg).".formatted(
            item.getName(), item.getWeightGrams() / 1000.0);
    }
}
