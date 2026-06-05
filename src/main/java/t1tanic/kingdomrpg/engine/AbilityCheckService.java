package t1tanic.kingdomrpg.engine;

import org.springframework.stereotype.Service;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;

/**
 * Resolves d20 ability checks and formats their output.
 * Shared by {@link t1tanic.kingdomrpg.engine.commands.AttemptCommand} (explicit player command)
 * and {@link t1tanic.kingdomrpg.engine.commands.TalkCommand} (implicit checks from conversation tone).
 *
 * @author t1tanic
 * @version 1.0
 */
@Service
public class AbilityCheckService {

    /**
     * The numeric trust adjustment applied on top of the LLM's tone-based delta.
     * Crit success and fumble have a larger swing; a plain failure is neutral here
     * since the LLM's [TRUST:N] already penalises the hostile tone.
     */
    public static final int TRUST_CRIT_SUCCESS = 6;
    public static final int TRUST_SUCCESS      = 3;
    public static final int TRUST_FAILURE      = -2;
    public static final int TRUST_FUMBLE       = -6;

    public record CheckResult(
            boolean success,
            boolean crit,
            boolean fumble,
            int     total,
            String  rollLine,
            String  narrative
    ) {
        public int trustDelta() {
            if (crit)   return TRUST_CRIT_SUCCESS;
            if (fumble) return TRUST_FUMBLE;
            return success ? TRUST_SUCCESS : TRUST_FAILURE;
        }
    }

    public CheckResult resolve(Player player, Ability ability) {
        int      mod    = player.getAttributes().modifier(ability.category().attribute());
        String   attr   = ability.category().attribute().abbrev();
        DiceRoll roll   = Dice.D20.roll().plus(mod);
        int      raw    = roll.rolls().get(0);
        int      total  = roll.total();
        boolean  crit   = roll.isCritical();
        boolean  fumble = roll.isFumble();
        boolean  success = crit || (!fumble && total >= ability.dc());

        String sign = mod >= 0 ? " + " : " - ";
        String base = "d20 → " + raw + sign + Math.abs(mod) + " (" + attr + ") = "
                    + MarkupTag.ITEM.wrap(String.valueOf(total));
        String rollLine;
        if (crit)        rollLine = base + "  — CRITICAL!";
        else if (fumble) rollLine = base + "  — FUMBLE!";
        else             rollLine = base + (success ? " ≥ " : " < ") + "DC " + ability.dc();

        String narrative;
        if (crit)        narrative = ability.successMsg() + "  An exceptional display.";
        else if (fumble) narrative = ability.failureMsg() + "  A spectacular mishap.";
        else if (success) narrative = ability.successMsg();
        else              narrative = ability.failureMsg();

        return new CheckResult(success, crit, fumble, total, rollLine, narrative);
    }

    public String format(Ability ability, CheckResult result) {
        String verdict;
        if (result.crit())        verdict = MarkupTag.EXIT.wrap("EXCEPTIONAL SUCCESS");
        else if (result.fumble()) verdict = MarkupTag.ITEM.wrap("SPECTACULAR FAILURE");
        else if (result.success()) verdict = MarkupTag.EXIT.wrap("SUCCESS");
        else                       verdict = "FAILURE";

        return "Attempting " + MarkupTag.ROOM.wrap(ability.displayName()) + "...\n\n"
             + MarkupTag.NERD.wrap(result.rollLine()) + "\n"
             + verdict + "\n"
             + result.narrative();
    }
}
