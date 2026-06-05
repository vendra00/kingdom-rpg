package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.NpcRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and rendering the player's immediate environmental surroundings.
 * <p>Shows NPCs currently present in the room (faction-colored and clickable to {@code talk}).
 * Named containers (from hidden items' {@code hiddenIn} field) are listed as interactive
 * environment objects, hinting that they can be investigated via {@code search}.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class LookCommand implements Command {

    private final ItemRepository itemRepository;
    private final NpcRepository  npcRepository;

    /**
     * {@inheritDoc}
     *
     * @param player the active player character observing their surroundings
     * @param args   trailing command arguments (unused by this command block)
     * @return a structured, multi-line string capturing the room scene, NPCs, and container hints
     */
    @Override
    public String execute(Player player, String[] args) {
        Room room = player.getCurrentRoom();
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(MarkupTag.ROOM.wrap(room.getName())).append(" ===\n");
        sb.append(MarkupTag.NARRATE.wrap(room.getDescription()));

        // NPCs present in the room
        List<Npc> npcs = npcRepository.findByCurrentRoomId(room.getId());
        if (!npcs.isEmpty()) {
            sb.append("\n");
            for (Npc npc : npcs) {
                sb.append("\n")
                  .append(MarkupTag.color(npc.getFaction().cssColor(), MarkupTag.NPC.wrap(npc.getName())))
                  .append("  ")
                  .append(MarkupTag.color("#888888", npc.getDescription()));
            }
        }

        // Hint at containers that hold hidden items, without revealing contents
        List<Item> hidden = itemRepository.findByRoomIdAndVisible(room.getId(), false);
        Set<String> containers = hidden.stream()
            .map(Item::getHiddenIn)
            .filter(h -> h != null && !h.isBlank())
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (!containers.isEmpty()) {
            sb.append("\n\nYou notice: ").append(
                containers.stream()
                    .map(c -> MarkupTag.CONTAINER.wrap(c))
                    .collect(Collectors.joining(", "))
            ).append("  ").append(MarkupTag.color("#555555", "(click or type: search <name>)"));
        }

        return sb.toString().stripTrailing();
    }
}
