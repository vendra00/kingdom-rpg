package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Equipment;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.*;
import t1tanic.kingdomrpg.domain.item.enums.EquipmentSlot;
import t1tanic.kingdomrpg.domain.item.enums.ItemTag;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and listing a player character's inventory content.
 * <p>Items are grouped by {@link ItemTag} category and rendered inside individual ASCII border boxes,
 * with colored condition and damage-type labels for equipment.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class InventoryCommand implements Command {

    private static final int    BOX_W  = 52;
    private static final int    CONT_W = BOX_W - 4;   // "│ " + " │"
    private static final String DASH   = "─";
    private static final String BOTTOM = "└" + DASH.repeat(BOX_W - 2) + "┘";

    private final ItemRepository itemRepository;

    /**
     * {@inheritDoc}
     *
     * @param player the active player character querying their carrying storage
     * @param args   trailing command arguments (unused by this command block)
     * @return a structured, boxed inventory sheet with colored stats and wear indicators
     */
    @Override
    public String execute(Player player, String[] args) {
        List<Item> items = itemRepository.findByPlayerId(player.getId());
        if (items.isEmpty()) return "Your inventory is empty.";

        Map<ItemTag, List<Item>> byTag = items.stream()
            .collect(Collectors.groupingBy(Item::getItemTag));

        Equipment eq = player.getEquipment();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ItemTag tag : ItemTag.values()) {
            List<Item> group = byTag.get(tag);
            if (group == null) continue;
            if (!first) sb.append("\n");
            first = false;
            sb.append(sectionHeader(tag.label()));
            for (Item item : group) {
                sb.append("\n").append(itemBox(item, eq));
            }
        }
        return sb.toString();
    }

    private String itemBox(Item item, Equipment eq) {
        StringBuilder sb = new StringBuilder();
        EquipmentSlot slot = eq.slotOf(item);
        String nameTag = MarkupTag.INVITEM.wrap(item.getName());
        if (slot != null) {
            nameTag += "  " + MarkupTag.color("#ffd700", "[E: " + slot.label() + "]");
        }
        sb.append(boxTop(nameTag)).append("\n");
        sb.append(boxLine(metaLine(item))).append("\n");

        String stats = statsLine(item);
        if (!stats.isEmpty()) sb.append(boxLine(stats)).append("\n");

        sb.append(boxLine(item.getDescription())).append("\n");
        sb.append(BOTTOM);
        return sb.toString();
    }

    private String metaLine(Item item) {
        String weight = "%.2f kg".formatted(item.getWeightGrams() / 1000.0);
        String base   = item.getTypeLabel() + "  ·  " + weight;
        if (item instanceof Consumable c) {
            return base + "  ·  Charges: " + c.getCharges();
        }
        if (item instanceof Weapon  w) return base + "  ·  Condition: " + MarkupTag.color(w.getCondition().cssColor(), w.getCondition().label());
        if (item instanceof Armor   a) return base + "  ·  Condition: " + MarkupTag.color(a.getCondition().cssColor(), a.getCondition().label());
        if (item instanceof Shield  s) return base + "  ·  Condition: " + MarkupTag.color(s.getCondition().cssColor(), s.getCondition().label());
        return base;
    }

    private String statsLine(Item item) {
        if (item instanceof Weapon w) {
            String dmg = w.getDamageType() != null
                ? MarkupTag.color(w.getDamageType().cssColor(), w.getDamageType().label()) : "—";
            return "Atk: %d–%d  ·  %s  ·  %s".formatted(
                w.getEffectiveAttackMin(), w.getEffectiveAttackMax(),
                w.getWeaponRange().label(), dmg);
        }
        if (item instanceof Armor  a) return "AC: %d  ·  %s".formatted(a.getEffectiveArmorClass(), a.getArmorType());
        if (item instanceof Shield s) return "Defense: +%d".formatted(s.getEffectiveDefenseBonus());
        return "";
    }

    private String sectionHeader(String label) {
        int dashes = BOX_W - label.length() - 4;
        return "── " + MarkupTag.ROOM.wrap(label) + " " + DASH.repeat(Math.max(1, dashes));
    }

    private String boxTop(String taggedName) {
        int dashes = BOX_W - visLen(taggedName) - 5;  // "┌─ " (3) + name + " " + dashes + "┐" (1)
        return "┌─ " + taggedName + " " + DASH.repeat(Math.max(1, dashes)) + "┐";
    }

    private String boxLine(String content) {
        int pad = CONT_W - visLen(content);
        return "│ " + content + " ".repeat(Math.max(0, pad)) + " │";
    }

    /** Strips all markup tags to compute the terminal-visible character width. */
    private int visLen(String s) {
        return s.replaceAll("\\[c=[^\\]]+\\]", "")
                .replace("[/c]", "")
                .replaceAll("\\[/?(item|invitem|room|exit|narrate|container)\\]", "")
                .length();
    }
}
