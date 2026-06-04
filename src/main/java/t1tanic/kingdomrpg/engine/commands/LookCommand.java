package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and rendering the player's immediate environmental surroundings.
 * <p>Shows only visible items. Named containers (from hidden items' {@code hiddenIn} field) are listed as
 * interactive environment objects, hinting that they can be investigated via {@code search}.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class LookCommand implements Command {

    private final ItemRepository itemRepository;

    /**
     * {@inheritDoc}
     *
     * @param player the active player character observing their surroundings
     * @param args   trailing command arguments (unused by this command block)
     * @return a structured, multi-line string capturing exits, visible items, and container hints
     */
    @Override
    public String execute(Player player, String[] args) {
        Room room = player.getCurrentRoom();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(MarkupTag.ROOM.wrap(room.getName())).append(" ===\n");
        sb.append(MarkupTag.NARRATE.wrap(room.getDescription()));

        // Hint at containers that hold hidden items, without revealing contents
        List<Item> hidden = itemRepository.findByRoomIdAndVisible(room.getId(), false);
        Set<String> containers = hidden.stream()
            .map(Item::getHiddenIn)
            .filter(h -> h != null && !h.isBlank())
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!containers.isEmpty()) {
            sb.append("\n\nYou notice: ").append(
                containers.stream()
                    .map(c -> MarkupTag.EXIT.wrap(c))
                    .collect(Collectors.joining(", "))
            ).append("  (search <name> to investigate)");
        }

        return sb.toString().stripTrailing();
    }
}
