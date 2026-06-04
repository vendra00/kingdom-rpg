package t1tanic.kingdomrpg.engine.commands;

import t1tanic.kingdomrpg.domain.character.Player;

public interface Command {
    String execute(Player player, String[] args);
}
