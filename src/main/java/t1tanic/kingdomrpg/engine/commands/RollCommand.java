package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command implementation responsible for parsing and executing arbitrary dice roll notations.
 * <p>This command intercepts standalone gaming dice requests, matches them against standard regular
 * expression formulas (supporting multi-dice structures and signed mathematical modifiers), evaluates
 * the numeric calculations through the underlying {@link Dice} systems, and returns a formatted arithmetic breakdown.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class RollCommand implements Command {

    /**
     * Regular expression tracking standard polyhedral tabletop dice notation.
     * Matches optional die quantity count parameters, required faced indices, and optional trailing math offsets
     * (e.g., matching groups across layouts like "2d6", "d20+5", or "1d8-2").
     */
    private static final Pattern NOTATION = Pattern.compile("(\\d*d\\d+)([+-]\\d+)?");

    /**
     * {@inheritDoc}
     * <p>Normalizes input text segments by stripping spaces, validates alignment against regex bounds,
     * applies signed trailing modifiers to calculations, and falls back to interactive documentation if
     * parsing exceptions or unrecognized die sizes occur.</p>
     *
     * @param player the active player character initiating the dice roll request
     * @param args   the argument segments containing the textual dice notation phrase
     * @return a structured string breaking down individual roll values, mod values, and calculation aggregates
     */
    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return """
                Usage: roll <notation>
                  roll d20        — roll a twenty-sided die
                  roll 2d6        — roll two six-sided dice
                  roll 1d20+5     — roll d20 and add a modifier
                Available dice: d4, d6, d8, d10, d12, d20, d100""";
        }

        String input = String.join("", args).toLowerCase().replaceAll("\\s+", "");
        Matcher m = NOTATION.matcher(input);
        if (!m.matches()) {
            return "Invalid notation '" + String.join(" ", args)
                + "'.  Try: roll 2d6  or  roll 1d20+3";
        }

        try {
            DiceRoll result = Dice.roll(m.group(1));
            if (m.group(2) != null) {
                result = result.plus(Integer.parseInt(m.group(2)));
            }
            return result.format();
        } catch (IllegalArgumentException e) {
            return e.getMessage() + ".  Available dice: d4, d6, d8, d10, d12, d20, d100";
        }
    }
}