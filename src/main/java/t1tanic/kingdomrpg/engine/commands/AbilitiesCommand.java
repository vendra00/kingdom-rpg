package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.domain.character.enums.AbilityCategory;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;

/**
 * Command implementation responsible for displaying the player's active Ability Book.
 * <p>This command aggregates available game skills by their respective categories,
 * computes current situational attribute modifiers based on the player's core stats,
 * and formats an aligned, scannable terminal overview complete with semantic text markup tags.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class AbilitiesCommand implements Command {

    /**
     * {@inheritDoc}
     * <p>Generates a multi-column visual index tracking abilities grouped under structural headers,
     * rendering descriptions, base Difficulty Classes (DCs), and relevant operational command suggestions.</p>
     *
     * @param player the active player character querying their ability matrix
     * @param args   trailing command arguments (unused by this command block)
     * @return a structured, marked-up text menu displaying categories, skills, modifiers, and DCs
     */
    @Override
    public String execute(Player player, String[] args) {
        var attrs = player.getAttributes();
        var sb    = new StringBuilder();

        sb.append(MarkupTag.ROOM.wrap("ABILITY BOOK"))
          .append("\n")
          .append("Type 'attempt <name>' to roll  —  e.g. ").append(MarkupTag.EXIT.wrap("attempt jump"))
          .append("\n");

        for (AbilityCategory cat : AbilityCategory.values()) {
            var abilities = Ability.byCategory(cat);
            if (abilities.isEmpty()) continue;

            int mod    = attrs.modifier(cat.attribute());
            String modStr = (mod >= 0 ? "+" : "") + mod;
            String attr   = cat.attribute().abbrev();

            sb.append("\n")
              .append(MarkupTag.ITEM.wrap(cat.displayName().toUpperCase()))
              .append("  ").append(attr).append(" ").append(modStr)
              .append("\n");

            for (Ability a : abilities) {
                // Pad the id inside the tag so columns stay aligned across rows
                String paddedId = String.format("%-13s", a.id());
                sb.append("  ")
                  .append(MarkupTag.EXIT.wrap(paddedId))
                  .append("  ")
                  .append(a.description())
                  .append("  DC ")
                  .append(a.dc())
                  .append("\n");
            }
        }

        return sb.toString().stripTrailing();
    }
}
