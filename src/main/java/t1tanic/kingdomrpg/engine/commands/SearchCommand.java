package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.CharacterAttributes;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.Attribute;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Command implementation for discovering hidden items within the player's current room.
 * <p>Two modes of operation:</p>
 * <ul>
 *   <li><b>No arguments</b> — performs a general perception scan (d20 + WIS modifier vs each
 *       hidden item's {@code perceptionDc}). Items whose DC is met or beaten become visible.</li>
 *   <li><b>With target</b> — directly searches a named container (e.g., {@code search chest}).
 *       All items whose {@code hiddenIn} matches the target are revealed with no roll required,
 *       simulating physically opening or rummaging through the object.</li>
 * </ul>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class SearchCommand implements Command {

    private final ItemRepository   itemRepository;
    private final PlayerRepository playerRepository;

    /**
     * {@inheritDoc}
     *
     * @param player the active player character performing the search
     * @param args   optional target container name tokens; empty triggers a full perception scan
     * @return a narrative string describing what was (or was not) discovered
     */
    @Override
    public String execute(Player player, String[] args) {
        long roomId = player.getCurrentRoom().getId();

        if (args.length == 0) {
            if (player.getScannedRoomIds().contains(roomId)) {
                return "You've already searched this room. What you found is what there is.";
            }
            // Container contents stay hidden until explicitly searched — only scan loose items
            List<Item> hidden = itemRepository.findByRoomIdAndVisible(roomId, false).stream()
                .filter(i -> i.getHiddenIn() == null || i.getHiddenIn().isBlank())
                .toList();
            if (hidden.isEmpty()) {
                player.getScannedRoomIds().add(roomId);
                playerRepository.save(player);
                return "You search the area thoroughly but find nothing out of the ordinary.";
            }
            return perceptionScan(player, hidden, roomId);
        }

        List<Item> hidden = itemRepository.findByRoomIdAndVisible(roomId, false);
        if (hidden.isEmpty()) {
            return "You search the area thoroughly but find nothing out of the ordinary.";
        }
        return containerSearch(String.join(" ", args), hidden);
    }

    private String perceptionScan(Player player, List<Item> hidden, long roomId) {
        int wisMod = CharacterAttributes.modifier(player.getAttributes().get(Attribute.WISDOM));
        DiceRoll roll = Dice.D20.roll().plus(wisMod);
        int total = roll.total();

        StringBuilder sb = new StringBuilder();
        sb.append("You scan the room carefully, senses alert...\n");
        sb.append(MarkupTag.NERD.wrap("Perception: " + roll.format())).append("\n");

        List<Item> found = new ArrayList<>();
        boolean stillHidden = false;

        for (Item item : hidden) {
            if (item.getPerceptionDc() == 0 || total >= item.getPerceptionDc()) {
                reveal(item);
                found.add(item);
            } else {
                stillHidden = true;
            }
        }

        // Lock this room — result stands regardless of outcome
        player.getScannedRoomIds().add(roomId);
        playerRepository.save(player);

        if (found.isEmpty()) {
            sb.append("\nYou sense something may be hidden here, but it eludes you.");
        } else {
            sb.append("\nYou discover:");
            for (Item item : found) {
                String where = item.getHiddenIn() != null
                    ? " — concealed in " + item.getHiddenIn() : "";
                sb.append("\n  · ").append(MarkupTag.ITEM.wrap(item.getName())).append(where);
            }
        }
        if (stillHidden) {
            sb.append("\n").append(MarkupTag.NARRATE.wrap("Something still eludes your senses..."));
        }
        return sb.toString();
    }

    private String containerSearch(String target, List<Item> hidden) {
        List<Item> inside = hidden.stream()
            .filter(i -> i.getHiddenIn() != null && matchesWord(i.getHiddenIn(), target))
            .toList();

        if (inside.isEmpty()) {
            return "You don't see any '" + target + "' here to search.";
        }

        String containerName = inside.get(0).getHiddenIn();
        inside.forEach(this::reveal);

        String found = inside.stream()
            .map(i -> MarkupTag.ITEM.wrap(i.getName()))
            .collect(Collectors.joining(", "));

        return "You rummage through " + containerName + "...\nYou find: " + found;
    }

    private static boolean matchesWord(String text, String word) {
        return Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE)
                      .matcher(text)
                      .find();
    }

    private void reveal(Item item) {
        item.setVisible(true);
        itemRepository.save(item);
    }
}
