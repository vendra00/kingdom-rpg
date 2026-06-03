package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.engine.MarkupTag;

@Component
public class CastCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Cast what?  (type 'spells' to see your cantrips)";
        }

        String input = String.join(" ", args).toLowerCase();

        return player.getLearnedCantrips().stream()
            .filter(c -> c.getName().toLowerCase().contains(input))
            .findFirst()
            .map(this::castFlavor)
            .orElse("You don't know that cantrip.  Type 'spells' to see what you have learned.");
    }

    private String castFlavor(Cantrip c) {
        String spellName = MarkupTag.ROOM.wrap(c.getName());
        String channel = switch (c.getEffect()) {
            case "damage"  -> "You focus your energy and shape the arcane threads of " + spellName + "...";
            case "debuff"  -> "Dark energy coils around your fingers as you invoke " + spellName + "...";
            case "buff"    -> "A warm shimmer envelops your hands as you channel " + spellName + "...";
            case "utility" -> "You weave the subtle patterns of " + spellName + "...";
            case "healing" -> "Soft light flows through you as you call upon " + spellName + "...";
            default        -> "You begin to cast " + spellName + "...";
        };

        String outcome = c.getDamageType() != null
            ? "\n" + MarkupTag.NARRATE.wrap(c.getDescription())
              + "\n\nNo enemy is present — the energy dissipates harmlessly."
              + "\n(Use " + MarkupTag.EXIT.wrap("attack") + " during combat to unleash this spell.)"
            : "\n" + MarkupTag.NARRATE.wrap(c.getDescription());

        return channel + outcome;
    }
}
