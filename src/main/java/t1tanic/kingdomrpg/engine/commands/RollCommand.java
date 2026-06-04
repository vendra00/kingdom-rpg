package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RollCommand implements Command {

    // Accepts: "d20", "2d6", "1d20+5", "d8-2"
    private static final Pattern NOTATION = Pattern.compile("(\\d*d\\d+)([+-]\\d+)?");

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
