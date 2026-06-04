package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for collecting items from the floor into the player's inventory.
 * <p>Only visible items can be picked up. Hidden items must first be revealed via {@link SearchCommand}.
 * The special argument {@code all} collects every visible item in the room in a single operation.</p>
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
     *
     * @param player the active player character attempting to pick up an item
     * @param args   item name tokens, or the single keyword {@code all}
     * @return confirmation of what was collected, or a failure reason
     */
    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) return "Take what?  (or 'take all')";

        if (args.length == 1 && args[0].equals("all")) {
            return takeAll(player);
        }

        String itemName = String.join(" ", args).toLowerCase();
        List<Item> visible = itemRepository.findByRoomIdAndVisible(
            player.getCurrentRoom().getId(), true);

        return visible.stream()
            .filter(item -> item.getName().toLowerCase().contains(itemName))
            .findFirst()
            .map(item -> pickUp(player, item))
            .orElse("There's no visible " + itemName + " here.");
    }

    private String takeAll(Player player) {
        List<Item> visible = itemRepository.findByRoomIdAndVisible(
            player.getCurrentRoom().getId(), true);
        if (visible.isEmpty()) return "There's nothing here to take.";

        List<String> taken   = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (Item item : visible) {
            int newCarry = player.getResources().getCarryWeight() + item.getWeightGrams();
            if (newCarry > player.getMaxCarryWeight()) {
                skipped.add(item.getName());
                log.debug("Skipping '{}' — would exceed carry limit", item.getName());
            } else {
                item.setRoom(null);
                item.setPlayer(player);
                itemRepository.save(item);
                player.getResources().setCarryWeight(newCarry);
                taken.add(item.getName());
            }
        }

        if (!taken.isEmpty()) playerRepository.save(player);

        StringBuilder sb = new StringBuilder();
        if (!taken.isEmpty()) {
            sb.append("You pick up: ").append(
                taken.stream().map(n -> MarkupTag.ITEM.wrap(n)).collect(Collectors.joining(", "))
            ).append(".");
        }
        if (!skipped.isEmpty()) {
            sb.append("\nToo heavy to carry: ").append(String.join(", ", skipped)).append(".");
        }
        return sb.toString().strip();
    }

    /**
     * Transfers a single visible item from the room floor into the player's inventory.
     *
     * @param player the operating player record
     * @param item   the target item to collect
     * @return a formatted confirmation or carry-limit rejection message
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
