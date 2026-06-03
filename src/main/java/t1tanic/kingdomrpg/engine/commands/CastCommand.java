package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.domain.Player;

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
        String channel = switch (c.getEffect()) {
            case "damage"  -> "You focus your energy and shape the arcane threads of [room]" + c.getName() + "[/room]...";
            case "debuff"  -> "Dark energy coils around your fingers as you invoke [room]" + c.getName() + "[/room]...";
            case "buff"    -> "A warm shimmer envelops your hands as you channel [room]" + c.getName() + "[/room]...";
            case "utility" -> "You weave the subtle patterns of [room]" + c.getName() + "[/room]...";
            case "healing" -> "Soft light flows through you as you call upon [room]" + c.getName() + "[/room]...";
            default        -> "You begin to cast [room]" + c.getName() + "[/room]...";
        };

        String outcome = c.getDamageType() != null
            ? "\n[narrate]" + c.getDescription() + "[/narrate]"
              + "\n\nNo enemy is present — the energy dissipates harmlessly."
              + "\n(Use [exit]attack[/exit] during combat to unleash this spell.)"
            : "\n[narrate]" + c.getDescription() + "[/narrate]";

        return channel + outcome;
    }
}
