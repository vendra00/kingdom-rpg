package t1tanic.kingdomrpg.engine.commands;

import t1tanic.kingdomrpg.domain.character.Player;

/**
 * Structural strategy contract defining an executable text command within the game engine loop.
 * <p>Classes implementing this interface encapsulate discrete textual player capabilities
 * (e.g., world movement, action rolls, inventory auditing). Commands parse optional contextual argument
 * tokens, manipulate associated domain entity states, and return feedback expressions structured for
 * upstream client console transport layers.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public interface Command {
    /**
     * Executes the domain business rules bound to the implemented terminal keyword.
     *
     * @param player the active player character instance initiating the command context
     * @param args   the raw positional text parameters parsed after the primary command invocation handle
     * @return a structured string containing formatted narrative, game logic readouts, or syntax error tips
     */
    String execute(Player player, String[] args);
}