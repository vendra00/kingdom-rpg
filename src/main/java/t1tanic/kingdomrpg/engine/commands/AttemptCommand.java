package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Ability;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.engine.MarkupTag;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;

@Component
public class AttemptCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Attempt what?  Type 'abilities' to see what you can try.";
        }

        String input = String.join(" ", args);
        return Ability.fromInput(input)
                      .map(a -> resolve(player, a))
                      .orElse("Unknown ability '" + input + "'.  Type 'abilities' to see the full list.");
    }

    private String resolve(Player player, Ability ability) {
        int    mod  = player.getAttributes().modifier(ability.category().attribute());
        String attr = ability.category().attribute().abbrev();

        DiceRoll roll   = Dice.D20.roll().plus(mod);
        int      raw    = roll.rolls().get(0);
        int      total  = roll.total();
        boolean  crit   = roll.isCritical();
        boolean  fumble = roll.isFumble();
        boolean  hit    = crit || (!fumble && total >= ability.dc());

        // ── Roll line ──────────────────────────────────────────────────────
        String sign = mod >= 0 ? " + " : " - ";
        String base = "d20 → " + raw + sign + Math.abs(mod) + " (" + attr + ") = "
                    + MarkupTag.ITEM.wrap(String.valueOf(total));
        String rollLine;
        if (crit) {
            rollLine = base + "  — CRITICAL!";
        } else if (fumble) {
            rollLine = base + "  — FUMBLE!";
        } else {
            rollLine = base + (hit ? " ≥ " : " < ") + "DC " + ability.dc();
        }

        // ── Outcome ────────────────────────────────────────────────────────
        String verdict;
        String narrative;
        if (crit) {
            verdict   = MarkupTag.EXIT.wrap("EXCEPTIONAL SUCCESS");
            narrative = ability.successMsg() + "  An exceptional display.";
        } else if (fumble) {
            verdict   = MarkupTag.ITEM.wrap("SPECTACULAR FAILURE");  // item tag = yellow = warning
            narrative = ability.failureMsg() + "  A spectacular mishap.";
        } else if (hit) {
            verdict   = MarkupTag.EXIT.wrap("SUCCESS");
            narrative = ability.successMsg();
        } else {
            verdict   = "FAILURE";
            narrative = ability.failureMsg();
        }

        return "Attempting " + MarkupTag.ROOM.wrap(ability.displayName()) + "...\n\n"
             + rollLine + "  →  " + verdict + "\n"
             + narrative;
    }
}
