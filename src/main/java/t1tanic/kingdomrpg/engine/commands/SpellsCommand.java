package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.magic.Cantrip;
import t1tanic.kingdomrpg.engine.MarkupTag;

import java.util.Comparator;
import java.util.Set;

/**
 * Command implementation responsible for displaying the player character's learned spell grimoire.
 * <p>This command aggregates all active {@link Cantrip} nodes bound to the character, sorts them
 * hierarchically by their structural school of magic and names, and renders a detailed layout highlighting
 * schools, damage categories, and instructional mechanical tooltips.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class SpellsCommand implements Command {

    /**
     * {@inheritDoc}
     * <p>Compiles a visual glossary of known zero-level spells. Checks for early empty-state exceptions
     * if the class has zero spell casting footprints, formats the entries with column spacing inside wrapping tags,
     * and appends quick-action code shortcuts to prompt downstream casting attempts.</p>
     *
     * @param player the active player character querying their spellbook inventory
     * @param args   trailing command arguments (unused by this command block)
     * @return a multi-line, formatted readout tracking spell school groupings, damage properties, and narrative summaries
     */
    @Override
    public String execute(Player player, String[] args) {
        Set<Cantrip> cantrips = player.getLearnedCantrips();
        if (cantrips.isEmpty()) {
            return "You have no cantrips. (No spells were selected during character creation.)";
        }

        StringBuilder sb = new StringBuilder("── Cantrips (cast at will) ───────────────────────\n\n");
        cantrips.stream()
            .sorted(Comparator.comparing(Cantrip::getSchool).thenComparing(Cantrip::getName))
            .forEach(c -> {
                String dmg = c.getDamageType() != null ? "  " + MarkupTag.ITEM.wrap(c.getDamageType()) : "";
                sb.append(MarkupTag.ROOM.wrap("%-20s".formatted(c.getName())))
                  .append(" [%s]%s\n".formatted(c.getSchool(), dmg));
                sb.append("  %s\n\n".formatted(c.getDescription()));
            });
        sb.append("Use: ").append(MarkupTag.EXIT.wrap("cast")).append(" <cantrip name>");
        return sb.toString();
    }
}