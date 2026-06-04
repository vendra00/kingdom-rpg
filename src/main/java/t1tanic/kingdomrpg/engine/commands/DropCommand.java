package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.List;

/**
 * Command implementation responsible for removing items from a player's inventory
 * and placing them onto the ground of their current location.
 * <p>This command updates the ownership references of the item, reduces the player's
 * accumulated encumbrance carry weight calculation, and commits both state transitions
 * across their respective persistence repositories.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DropCommand implements Command {

    private final ItemRepository   itemRepository;
    private final PlayerRepository playerRepository;

    /**
     * {@inheritDoc}
     * <p>Collates sequential arguments to form a target item query phrase. Searches the
     * player's active storage graph using case-insensitive partial matching, handling
     * early exit states if the query string or item collection is empty.</p>
     *
     * @param player the active player character initiating the drop sequence
     * @param args   the clean argument array containing the name or keyword of the item
     * @return a descriptive confirmation text block indicating item weight and removal outcome
     */
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

    /**
     * Transfers ownership of an item from the player to the environmental room layout.
     * <p>Detaches the item's relational player reference, binds it to the current geographical
     * {@link t1tanic.kingdomrpg.domain.world.Room} block, and substracts the mass signature
     * from the user's active encumbrance metrics.</p>
     *
     * @param player the operating player record modifying inventory resources
     * @param item   the target item instance to unequip and place on the ground
     * @return a formatted text statement detailing the item dropped and its calculated weight in kilograms
     */
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