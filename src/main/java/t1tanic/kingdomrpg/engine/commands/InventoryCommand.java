package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InventoryCommand implements Command {

    private final ItemRepository itemRepository;

    @Override
    public String execute(Player player, String[] args) {
        List<Item> items = itemRepository.findByPlayerId(player.getId());
        if (items.isEmpty()) {
            return "Your inventory is empty.";
        }
        return "Inventory:\n" + items.stream()
            .map(item -> "  - [item]%s[/item] (%.2f kg): %s".formatted(
                item.getName(), item.getWeightGrams() / 1000.0, item.getDescription()))
            .collect(Collectors.joining("\n"));
    }
}
