package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.magic.Cantrip;
import t1tanic.kingdomrpg.engine.MarkupTag;

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
                String dmg = c.getDamageType() != null ? "  " + MarkupTag.ITEM.wrap(c.getDamageType()) : "";
                sb.append(MarkupTag.ROOM.wrap("%-20s".formatted(c.getName())))
                  .append(" [%s]%s\n".formatted(c.getSchool(), dmg));
                sb.append("  %s\n\n".formatted(c.getDescription()));
            });
        sb.append("Use: ").append(MarkupTag.EXIT.wrap("cast")).append(" <cantrip name>");
        return sb.toString();
    }
}
