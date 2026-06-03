package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.CharacterAttributes;
import t1tanic.kingdomrpg.domain.Player;

@Component
public class StatusCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        var id    = player.getIdentity();
        var attrs = player.getAttributes();
        var res   = player.getResources();

        String title = "%s  ·  %s %s".formatted(
            player.getName(),
            cap(id.getRace()),
            cap(id.getCharacterClass())
        );
        String subline = "Background: %s  ·  %s  ·  %s".formatted(
            cap(id.getBackground()),
            player.getCurrentRoom().getName(),
            cap(id.getGender())
        );
        return """
            [room]%s[/room]
            %s

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
            title, subline,
            res.getHealth(),  player.getMaxHealth(),  bar(res.getHealth(),  player.getMaxHealth()),
            res.getMana(),    player.getMaxMana(),    bar(res.getMana(),    player.getMaxMana()),
            res.getStamina(), player.getMaxStamina(), bar(res.getStamina(), player.getMaxStamina()),
            attrs.getStrength(),     CharacterAttributes.modifier(attrs.getStrength()),
            attrs.getDexterity(),    CharacterAttributes.modifier(attrs.getDexterity()),
            attrs.getConstitution(), CharacterAttributes.modifier(attrs.getConstitution()),
            attrs.getIntelligence(), CharacterAttributes.modifier(attrs.getIntelligence()),
            attrs.getWisdom(),       CharacterAttributes.modifier(attrs.getWisdom()),
            attrs.getCharisma(),     CharacterAttributes.modifier(attrs.getCharisma())
        );
    }

    private String bar(int current, int max) {
        int filled = max > 0 ? Math.max(0, Math.min(10, Math.round((float) current / max * 10))) : 0;
        return "█".repeat(filled) + "░".repeat(10 - filled);
    }

    private String cap(String s) {
        if (s == null || s.isBlank()) return "—";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}
