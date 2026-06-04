package t1tanic.kingdomrpg.engine.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.magic.Cantrip;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;

/**
 * Command implementation responsible for executing spelling gestures and casting learned cantrips.
 * <p>This command verifies if a specified spell keyword matches the character's spellbook matrix,
 * prints structural channeling visual flavor blocks, computes non-combat utility or offensive dice
 * evaluations, and handles safe energy dissipation rules outside of formal combat contexts.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Slf4j
@Component
public class CastCommand implements Command {

    /**
     * {@inheritDoc}
     * <p>Collates sequential arguments to construct a flexible keyword text query. Filters across
     * the player's learned collection using case-insensitive contains logic; triggers a magic failure
     * prompt if no match is resolved.</p>
     *
     * @param player the active spellcasting player character instance
     * @param args   the clean argument components containing the textual name of the targeted cantrip
     * @return a multi-line output detailing channeling text logs, dice roll values, and structural game state descriptors
     */
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

    /**
     * Builds atmospheric text strings and evaluates mechanical damage properties associated with a resolved spell.
     * <p>If the targeted cantrip contains a non-null structural damage dice property, an immediate dice parse and
     * roll pipeline is initiated. Because this action command executes in environmental non-combat states,
     * damages are visually logged before safely dissipating away into the ambient background.</p>
     *
     * @param c the target {@link Cantrip} blueprint layout entity to cast
     * @return a fully styled, marked-up text block suitable for console output
     */
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