package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.item.enums.ItemTag;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and listing a player character's inventory content.
 * <p>This command queries active item assets bound to the player ID, segments them into categories based on
 * their structural {@link ItemTag}, calculates metric weights into fractional kilograms, and renders a
 * marked-up terminal display sheet summarizing descriptions and gear types.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class InventoryCommand implements Command {

    private final ItemRepository itemRepository;

    /**
     * {@inheritDoc}
     * <p>Groups item entities by category and processes each cluster into structural display lines. Special
     * item sub-labels (e.g., specific equipment types) are extracted dynamically and styled with independent markup tags.</p>
     *
     * @param player the active player character querying their carrying storage
     * @param args   trailing command arguments (unused by this command block)
     * @return a structured, marked-up string breakdown detailing item weights, descriptions, and functional types
     */
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