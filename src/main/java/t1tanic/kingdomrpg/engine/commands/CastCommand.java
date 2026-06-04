package t1tanic.kingdomrpg.engine.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.engine.MarkupTag;

@Slf4j
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
        log.debug("Casting '{}' ({})", c.getName(), c.getEffect());
        String spellName = MarkupTag.ROOM.wrap(c.getName());
        String channel   = c.getEffect().channel(spellName);

        String outcome = c.getDamageType() != null
            ? "\n" + MarkupTag.NARRATE.wrap(c.getDescription())
              + "\n\nNo enemy is present — the energy dissipates harmlessly."
              + "\n(Use " + MarkupTag.EXIT.wrap("attack") + " during combat to unleash this spell.)"
            : "\n" + MarkupTag.NARRATE.wrap(c.getDescription());

        return channel + outcome;
    }
}
