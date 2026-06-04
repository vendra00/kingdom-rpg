package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;

/**
 * Command implementation responsible for executing contextual physical or mental skill checks.
 * <p>This command matches user-entered text arguments against registered {@link Ability} sets,
 * calculates situational character attribute adjustments, fires an underlying d20 calculation roll,
 * and resolves the action outcome against the targeted Difficulty Class (DC), providing contextual narrative
 * feedback on exceptional successes (criticals) or fumbles.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class AttemptCommand implements Command {

    /**
     * {@inheritDoc}
     * <p>Parses sequential argument segments into a cohesive key phrase to search for an ability.
     * If found, the challenge resolution pipeline begins; otherwise, an interactive troubleshooting tip is returned.</p>
     *
     * @param player the active player character attempting the action skill check
     * @param args   the clean argument components containing the textual name of the targeted ability
     * @return a structured string log breaking down the dynamic dice operations, status thresholds, and narrative result
     */
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

    /**
     * Executes the mechanical d20 evaluation loop against an explicitly identified ability template.
     * <p>Pulls the character modifier matching the core attribute requirement, applies it additively
     * to a single twentieth-faced polyhedral dice throw, and scores validation parameters by following
     * standard d20/D&D rule sets (critical thresholds override raw totals; fumbles automatically fail).</p>
     *
     * @param player  the operating player instance providing baseline attributes
     * @param ability the targeted capability template being verified
     * @return a multi-line formatted readout highlighting roll math calculations and narrative outcomes
     */
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
