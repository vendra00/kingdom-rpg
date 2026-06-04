package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.ItemTag;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.engine.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.Map;
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

        Map<ItemTag, List<Item>> byTag = items.stream()
            .collect(Collectors.groupingBy(Item::getItemTag));

        StringBuilder sb = new StringBuilder("Inventory:");
        for (ItemTag tag : ItemTag.values()) {
            List<Item> group = byTag.get(tag);
            if (group == null) continue;
            sb.append("\n  ").append(MarkupTag.ROOM.wrap(tag.label())).append(":");
            for (Item item : group) {
                String typeLabel = tag == ItemTag.EQUIPMENT
                    ? " " + MarkupTag.EXIT.wrap(item.getTypeLabel())
                    : "";
                sb.append("\n    - [item]%s[/item]%s (%.2f kg): %s".formatted(
                    item.getName(), typeLabel, item.getWeightGrams() / 1000.0, item.getDescription()));
            }
        }
        return sb.toString();
    }
}
