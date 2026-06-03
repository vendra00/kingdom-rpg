package t1tanic.kingdomrpg.engine.commands;

import t1tanic.kingdomrpg.domain.Player;

public interface Command {
    String execute(Player player, String[] args);
}
