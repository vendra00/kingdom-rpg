package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Player;

@Component
public class StatusCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        return """
            [room]%s[/room]  —  %s

            HP       %3d / %-3d  %s
            Mana     %3d / %-3d  %s
            Stamina  %3d / %-3d  %s

            ── Attributes ────────────────────
              Strength      %2d  (%+d)
              Dexterity     %2d  (%+d)
              Constitution  %2d  (%+d)
              Intelligence  %2d  (%+d)
              Wisdom        %2d  (%+d)
              Charisma      %2d  (%+d)""".formatted(
            player.getName(),
            player.getCurrentRoom().getName(),
            player.getHealth(),    player.getMaxHealth(),  bar(player.getHealth(),  player.getMaxHealth()),
            player.getMana(),      player.getMaxMana(),    bar(player.getMana(),    player.getMaxMana()),
            player.getStamina(),   player.getMaxStamina(), bar(player.getStamina(), player.getMaxStamina()),
            player.getStrength(),     player.modifier(player.getStrength()),
            player.getDexterity(),    player.modifier(player.getDexterity()),
            player.getConstitution(), player.modifier(player.getConstitution()),
            player.getIntelligence(), player.modifier(player.getIntelligence()),
            player.getWisdom(),       player.modifier(player.getWisdom()),
            player.getCharisma(),     player.modifier(player.getCharisma())
        );
    }

    private String bar(int current, int max) {
        int filled = max > 0 ? Math.round((float) current / max * 10) : 0;
        filled = Math.max(0, Math.min(10, filled));
        return "█".repeat(filled) + "░".repeat(10 - filled);
    }
}
