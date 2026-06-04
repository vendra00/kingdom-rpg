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
public class TakeCommand implements Command {

    private final ItemRepository   itemRepository;
    private final PlayerRepository playerRepository;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) return "Take what?";

        String itemName = String.join(" ", args).toLowerCase();
        List<Item> roomItems = itemRepository.findByRoomId(player.getCurrentRoom().getId());

        return roomItems.stream()
            .filter(item -> item.getName().toLowerCase().contains(itemName))
            .findFirst()
            .map(item -> pickUp(player, item))
            .orElse("There's no " + itemName + " here.");
    }

    private String pickUp(Player player, Item item) {
        int newCarry = player.getResources().getCarryWeight() + item.getWeightGrams();
        if (newCarry > player.getMaxCarryWeight()) {
            log.warn("Carry limit exceeded — '{}' ({} g) would bring total to {} g / {} g max",
                item.getName(), item.getWeightGrams(), newCarry, player.getMaxCarryWeight());
            return "Too heavy! You can't carry the %s (%.2f kg). Carrying %.2f / %.2f kg.".formatted(
                item.getName(),
                item.getWeightGrams()        / 1000.0,
                player.getResources().getCarryWeight() / 1000.0,
                player.getMaxCarryWeight()   / 1000.0
            );
        }
        log.debug("Picked up '{}' ({} g) — carrying {}/{} g",
            item.getName(), item.getWeightGrams(), newCarry, player.getMaxCarryWeight());
        item.setRoom(null);
        item.setPlayer(player);
        itemRepository.save(item);

        player.getResources().setCarryWeight(newCarry);
        playerRepository.save(player);

        return "You pick up the %s (%.2f kg).".formatted(
            item.getName(), item.getWeightGrams() / 1000.0);
    }
}
