package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Attribute;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.engine.MarkupTag;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class StatusCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        var id    = player.getIdentity();
        var attrs = player.getAttributes();
        var res   = player.getResources();

        String title = MarkupTag.ROOM.wrap("%s  ·  %s %s".formatted(
            player.getName(),
            cap(id.getRace()),
            cap(id.getCharacterClass())
        ));
        String subline = "Background: %s  ·  %s  ·  %s".formatted(
            cap(id.getBackground()),
            player.getCurrentRoom().getName(),
            cap(id.getGender())
        );
        String attrSection = Arrays.stream(Attribute.values())
            .map(attr -> "  %-14s%2d  (%+d)".formatted(
                cap(attr.key()), attrs.get(attr), attrs.modifier(attr)))
            .collect(Collectors.joining("\n"));

        return """
            %s
            %s

            HP       %3d / %-3d  %s
            Mana     %3d / %-3d  %s
            Stamina  %3d / %-3d  %s
            Carry    %.2f / %.2f kg  %s

            ── Attributes ────────────────────
            %s""".formatted(
            title, subline,
            res.getHealth(),  player.getMaxHealth(),  bar(res.getHealth(),  player.getMaxHealth()),
            res.getMana(),    player.getMaxMana(),    bar(res.getMana(),    player.getMaxMana()),
            res.getStamina(), player.getMaxStamina(), bar(res.getStamina(), player.getMaxStamina()),
            res.getCarryWeight()       / 1000.0,
            player.getMaxCarryWeight() / 1000.0,
            bar(res.getCarryWeight(),  player.getMaxCarryWeight()),
            attrSection
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
