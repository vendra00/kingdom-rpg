package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Player;

@Component
public class HelpCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        return """
            Available commands:
              look              - Examine your surroundings
              go [direction]    - Move (north, south, east, west) — or just type the direction
              take [item]       - Pick up an item
              drop [item]       - Drop an item from your inventory
              inventory / inv   - Check your inventory
              status            - View your character stats
              spells            - List your cantrips
              cast [cantrip]    - Cast a cantrip
              help              - Show this message""";
    }
}
