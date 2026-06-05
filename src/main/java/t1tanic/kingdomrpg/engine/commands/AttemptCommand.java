package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.engine.AbilityCheckService;

/**
 * Handles the player's explicit {@code attempt <ability>} command.
 * Delegates resolution and formatting to {@link AbilityCheckService}.
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class AttemptCommand implements Command {

    private final AbilityCheckService abilityCheckService;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Attempt what?  Type 'abilities' to see what you can try.";
        }
        String input = String.join(" ", args);
        return Ability.fromInput(input)
                      .map(a -> a.isConversationOnly()
                              ? "'" + a.displayName() + "' plays out through conversation — try: talk <npc> <message>"
                              : abilityCheckService.format(a, abilityCheckService.resolve(player, a)))
                      .orElse("Unknown ability '" + input + "'.  Type 'abilities' to see the full list.");
    }
}
