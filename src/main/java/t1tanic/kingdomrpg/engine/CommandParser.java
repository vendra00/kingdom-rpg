package t1tanic.kingdomrpg.engine;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Player;
import t1tanic.kingdomrpg.engine.commands.*;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CommandParser {

    private final LookCommand      lookCommand;
    private final GoCommand        goCommand;
    private final TakeCommand      takeCommand;
    private final DropCommand      dropCommand;
    private final InventoryCommand inventoryCommand;
    private final StatusCommand    statusCommand;
    private final SpellsCommand    spellsCommand;
    private final CastCommand      castCommand;
    private final HelpCommand      helpCommand;
    private final MeterRegistry    meterRegistry;

    public String parse(Player player, String input) {
        if (input == null || input.isBlank()) {
            return "Hm? (type 'help' for commands)";
        }

        String[] parts = input.trim().toLowerCase().split("\\s+");
        String   verb  = parts[0];
        String[] args  = Arrays.copyOfRange(parts, 1, parts.length);

        return meterRegistry.timer("game.command.duration", "verb", verb)
                            .record(() -> dispatch(player, verb, args));
    }

    private String dispatch(Player player, String verb, String[] args) {
        return switch (verb) {
            case "look", "l"                         -> lookCommand.execute(player, args);
            case "go", "move"                        -> goCommand.execute(player, args);
            case "north", "n"                        -> goCommand.execute(player, new String[]{"north"});
            case "south", "s"                        -> goCommand.execute(player, new String[]{"south"});
            case "east",  "e"                        -> goCommand.execute(player, new String[]{"east"});
            case "west",  "w"                        -> goCommand.execute(player, new String[]{"west"});
            case "take", "get", "pick"               -> takeCommand.execute(player, args);
            case "drop"                              -> dropCommand.execute(player, args);
            case "inventory", "inv", "i"             -> inventoryCommand.execute(player, args);
            case "status", "stats"                   -> statusCommand.execute(player, args);
            case "spells", "cantrips", "grimoire"    -> spellsCommand.execute(player, args);
            case "cast", "use"                       -> castCommand.execute(player, args);
            case "help", "?"                         -> helpCommand.execute(player, args);
            default -> "I don't understand '" + verb + "'. Type 'help' for commands.";
        };
    }
}
