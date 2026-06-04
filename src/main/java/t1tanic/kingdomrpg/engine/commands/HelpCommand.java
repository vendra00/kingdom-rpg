package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;

/**
 * Command implementation responsible for displaying a structured index of actionable keywords.
 * <p>This command acts as an interactive reference sheet, providing players with clear usage summaries,
 * movement syntax guidelines, and syntactic shortcuts for core game engine mechanics.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class HelpCommand implements Command {

    /**
     * {@inheritDoc}
     *
     * @param player the active player character querying structural command guidelines
     * @param args   trailing command arguments (unused by this command block)
     * @return a multi-line, preformatted string documenting available text actions and command shorthand variations
     */
    @Override
    public String execute(Player player, String[] args) {
        return """
            Available commands:
              look              - Examine your surroundings
              go [direction]    - Move (north, south, east, west) — or just type the direction
              take [item]       - Pick up an item
              drop [item]       - Drop an item from your inventory
              inventory / inv   - Check your inventory
              search            - Search for items and secrets in the room
              status            - View your character stats
              spells            - List your cantrips
              cast [cantrip]    - Cast a cantrip
              roll [notation]   - Roll dice  (d20, 2d6, 1d8+3 …)
              abilities / hab   - Open your ability book
              attempt [name]    - Attempt an ability check
              help              - Show this message""";
    }
}