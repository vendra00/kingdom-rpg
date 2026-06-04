package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Armor;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.item.Shield;
import t1tanic.kingdomrpg.domain.item.Weapon;
import t1tanic.kingdomrpg.domain.item.enums.ItemTag;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and listing a player character's inventory content.
 * <p>This command queries active item assets bound to the player ID, segments them into categories based on
 * their structural {@link ItemTag}, calculates metric weights into fractional kilograms, and renders a
 * marked-up terminal display sheet summarizing descriptions, gear stats, and item condition.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class InventoryCommand implements Command {

    private final ItemRepository itemRepository;

    /**
     * {@inheritDoc}
     * <p>Groups item entities by category and processes each cluster into structural display lines. Equipment
     * items additionally render condition-scaled combat statistics and current wear state.</p>
     *
     * @param player the active player character querying their carrying storage
     * @param args   trailing command arguments (unused by this command block)
     * @return a structured, marked-up string breakdown detailing item weights, descriptions, stats, and condition
     */
    @Override
    public String execute(Player player, String[] args) {
        List<Item> items = itemRepository.findByPlayerId(player.getId());
        if (items.isEmpty()) {
            return "Your inventory is empty.";
        }

        Map<ItemTag, List<Item>> byTag = items.stream()
            .collect(Collectors.groupingBy(Item::getItemTag));

        StringBuilder sb = new StringBuilder("Inventory:");
        for (ItemTag tag : ItemTag.values()) {
            List<Item> group = byTag.get(tag);
            if (group == null) continue;
            sb.append("\n  ").append(MarkupTag.ROOM.wrap(tag.label())).append(":");
            for (Item item : group) {
                String typeLabel = tag == ItemTag.EQUIPMENT
                    ? " " + MarkupTag.EXIT.wrap(item.getTypeLabel())
                    : "";
                sb.append("\n    - [item]%s[/item]%s (%.2f kg): %s".formatted(
                    item.getName(), typeLabel, item.getWeightGrams() / 1000.0, item.getDescription()));
                sb.append(equipmentStats(item));
            }
        }
        return sb.toString();
    }

    private String equipmentStats(Item item) {
        if (item instanceof Weapon w) {
            String dmg = w.getDamageType() != null
                ? MarkupTag.color(w.getDamageType().cssColor(), w.getDamageType().label())
                : "—";
            String cond = MarkupTag.color(w.getCondition().cssColor(), w.getCondition().label());
            return "\n      Atk: %d–%d  ·  %s  ·  %s  ·  Condition: %s".formatted(
                w.getEffectiveAttackMin(), w.getEffectiveAttackMax(),
                w.getWeaponRange().label(), dmg, cond);
        }
        if (item instanceof Armor a) {
            String cond = MarkupTag.color(a.getCondition().cssColor(), a.getCondition().label());
            return "\n      AC: %d  ·  %s  ·  Condition: %s".formatted(
                a.getEffectiveArmorClass(), a.getArmorType(), cond);
        }
        if (item instanceof Shield s) {
            String cond = MarkupTag.color(s.getCondition().cssColor(), s.getCondition().label());
            return "\n      Defense: +%d  ·  Condition: %s".formatted(
                s.getEffectiveDefenseBonus(), cond);
        }
        return "";
    }
}
