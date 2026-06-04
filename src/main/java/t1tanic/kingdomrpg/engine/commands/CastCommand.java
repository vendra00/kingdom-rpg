package t1tanic.kingdomrpg.engine.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.magic.Cantrip;
import t1tanic.kingdomrpg.engine.MarkupTag;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;

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

        String outcome;
        if (c.getDamageDice() != null) {
            DiceRoll roll = Dice.roll(c.getDamageDice());
            String dmgLine = roll.format() + " " + c.getDamageType() + " damage";
            outcome = "\n" + MarkupTag.NARRATE.wrap(c.getDescription())
                + "\n\nDamage roll — " + MarkupTag.ITEM.wrap(dmgLine)
                + "\n(No enemy present — the energy dissipates harmlessly."
                + "  Use " + MarkupTag.EXIT.wrap("attack") + " during combat.)";
        } else {
            outcome = "\n" + MarkupTag.NARRATE.wrap(c.getDescription());
        }

        return channel + outcome;
    }
}
