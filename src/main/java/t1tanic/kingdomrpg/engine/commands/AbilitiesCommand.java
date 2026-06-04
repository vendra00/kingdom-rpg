package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Ability;
import t1tanic.kingdomrpg.domain.AbilityCategory;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.engine.MarkupTag;

@Component
public class AbilitiesCommand implements Command {

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
