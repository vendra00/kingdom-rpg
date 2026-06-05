package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Equipment;
import t1tanic.kingdomrpg.domain.character.enums.Attribute;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.item.enums.EquipmentSlot;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and rendering a comprehensive character sheet layout.
 * <p>This command pulls the character's primary identity information, dynamic resource vitals (Health, Mana, Stamina),
 * current carrying capacity, and core ability attributes. It then generates visual progress bars and formatted data column
 * metrics wrapped inside terminal markup descriptors.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class StatusCommand implements Command {

    /**
     * {@inheritDoc}
     * <p>Assembles character attributes and structural parameters into an explicit multi-line profile interface display.
     * Handles string token capitalization normalization and calculates relative resource bar fill fractions.</p>
     *
     * @param player the active player character querying their sheet status
     * @param args   trailing command arguments (unused by this command block)
     * @return a multi-line character overview sheet detailing metadata, structural resource indicators, and attribute modifiers
     */
    @Override
    public String execute(Player player, String[] args) {
        var id    = player.getIdentity();
        var attrs = player.getAttributes();
        var res   = player.getResources();

        String title   = MarkupTag.ROOM.wrap(player.getName() + "  ·  " + id.getRace().displayName() + " " + id.getCharacterClass().displayName());
        String subline = "Background: " + id.getBackground().displayName() + "  ·  " + player.getCurrentRoom().getName() + "  ·  " + cap(id.getGender());

        String hpCol  = hpColor(res.getHealth(),  player.getMaxHealth());
        String vitals =
            "  HP      " + MarkupTag.color(hpCol,    bar(res.getHealth(),  player.getMaxHealth()))
                         + "  " + MarkupTag.color(hpCol,    res.getHealth()  + " / " + player.getMaxHealth())  + "\n" +
            "  Mana    " + MarkupTag.color("#00bfff", bar(res.getMana(),    player.getMaxMana()))
                         + "  " + MarkupTag.color("#00bfff", res.getMana()    + " / " + player.getMaxMana())    + "\n" +
            "  Stamina " + MarkupTag.color("#ffd700", bar(res.getStamina(), player.getMaxStamina()))
                         + "  " + MarkupTag.color("#ffd700", res.getStamina() + " / " + player.getMaxStamina()) + "\n" +
            "  Carry   " + bar(res.getCarryWeight(), player.getMaxCarryWeight())
                         + "  " + String.format(Locale.US, "%.2f / %.2f kg", res.getCarryWeight() / 1000.0, player.getMaxCarryWeight() / 1000.0) + "\n" +
            "  Gold    " + MarkupTag.color("#ffd700", "⬡ " + res.getGold() + " gold");

        String attrSection = Arrays.stream(Attribute.values())
            .map(attr -> "  %-14s%2d  (%+d)".formatted(cap(attr.key()), attrs.get(attr), attrs.modifier(attr)))
            .collect(Collectors.joining("\n"));

        String equippedSection = equippedSection(player.getEquipment());

        return "%s\n%s\n\n%s\n\n── Attributes ─────────────────────\n%s\n\n── Equipped ────────────────────────\n%s"
            .formatted(title, subline, vitals, attrSection, equippedSection);
    }

    private String equippedSection(Equipment eq) {
        StringBuilder sb = new StringBuilder();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item item = eq.getSlot(slot);
            String slotLabel = "  %-10s".formatted(slot.label());
            if (item != null) {
                sb.append(slotLabel).append(MarkupTag.EQUIPPED.wrap(item.getName())).append("\n");
            } else {
                sb.append(slotLabel).append(MarkupTag.color("#555555", "— empty —")).append("\n");
            }
        }
        return sb.toString().stripTrailing();
    }

    private String hpColor(int hp, int max) {
        double pct = max > 0 ? (double) hp / max : 1.0;
        if (pct > 0.50) return "#00ff41";
        if (pct > 0.25) return "#ffd700";
        return "#ff4444";
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