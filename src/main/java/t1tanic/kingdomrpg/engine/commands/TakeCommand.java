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
 * Command implementation responsible for allowing players to collect items from the ground
 * of their current room and place them into their inventory.
 * <p>This command verifies that the requested item exists in the location, performs a safety check
 * against the character's structural maximum carry capacity limits, updates entity relationships,
 * and commits the resulting transactional state changes back to the database tier.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TakeCommand implements Command {

    private final ItemRepository   itemRepository;
    private final PlayerRepository playerRepository;

    /**
     * {@inheritDoc}
     * <p>Collates sequential arguments to construct a target item query phrase. Searches the
     * environmental location's ground inventory using case-insensitive partial matching logic,
     * handling immediate exit scenarios if the query array is empty.</p>
     *
     * @param player the active player character attempting to pick up an item
     * @param args   the clean argument components containing the textual name or keyword of the item
     * @return a structured string confirmation detailing the collection result or capacity failure rules
     */
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

    /**
     * Transfers ownership of an item from the environmental floor to the player's direct inventory graph.
     * <p>Evaluates weight thresholds; if the added item mass breaks structural carrying constraints,
     * the transaction gets rejected with a warning log and an in-game error prompt. Otherwise, the item's
     * room relationship is cleared, it is bound to the player instance, and encumbrance values are incremented.</p>
     *
     * @param player the operating player record modifying inventory resources
     * @param item   the target item instance to collect from the floor
     * @return a formatted text statement detailing the item acquired and its calculated weight in kilograms
     */
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