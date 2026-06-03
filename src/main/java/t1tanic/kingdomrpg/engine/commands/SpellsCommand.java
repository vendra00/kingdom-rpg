package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.domain.Player;

import java.util.Comparator;
import java.util.Set;

@Component
public class SpellsCommand implements Command {

    @Override
    public String execute(Player player, String[] args) {
        Set<Cantrip> cantrips = player.getLearnedCantrips();
        if (cantrips.isEmpty()) {
            return "You have no cantrips. (No spells were selected during character creation.)";
        }

        StringBuilder sb = new StringBuilder("── Cantrips (cast at will) ───────────────────────\n\n");
        cantrips.stream()
            .sorted(Comparator.comparing(Cantrip::getSchool).thenComparing(Cantrip::getName))
            .forEach(c -> {
                String dmg = c.getDamageType() != null ? "  [item]" + c.getDamageType() + "[/item]" : "";
                sb.append("[room]%-20s[/room] [%s]%s\n".formatted(c.getName(), c.getSchool(), dmg));
                sb.append("  %s\n\n".formatted(c.getDescription()));
            });
        sb.append("Use: [exit]cast[/exit] <cantrip name>");
        return sb.toString();
    }
}
