package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Equipment;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.*;
import t1tanic.kingdomrpg.domain.item.enums.EquipmentSlot;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.List;

/**
 * Equips an item from the player's inventory into the appropriate body slot.
 * <p>Slot assignment is derived from the item type: {@link Weapon} → Main Hand,
 * {@link Shield} → Off Hand, {@link Armor} → Body. If the target slot is already
 * occupied the previous item is silently swapped back into the inventory.</p>
 */
@Component
@RequiredArgsConstructor
public class EquipCommand implements Command {

    private final ItemRepository   itemRepository;
    private final PlayerRepository playerRepository;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) return "Equip what?";

        String name = String.join(" ", args).toLowerCase();
        List<Item> inventory = itemRepository.findByPlayerId(player.getId());

        return inventory.stream()
            .filter(i -> i.getName().toLowerCase().contains(name))
            .findFirst()
            .map(item -> equip(player, item))
            .orElse("You don't have a '" + name + "' in your inventory.");
    }

    private String equip(Player player, Item item) {
        EquipmentSlot slot = slotFor(item);
        if (slot == null) {
            return "You can't equip the " + MarkupTag.ITEM.wrap(item.getName()) + ".";
        }
        if (item.isBroken()) {
            return "The " + MarkupTag.ITEM.wrap(item.getName()) + " is broken and cannot be equipped.";
        }

        Equipment eq      = player.getEquipment();
        Item      current = eq.getSlot(slot);

        eq.setSlot(slot, item);
        playerRepository.save(player);

        if (current != null && !current.getId().equals(item.getId())) {
            return "You swap out the " + MarkupTag.ITEM.wrap(current.getName())
                + " and equip the " + MarkupTag.ITEM.wrap(item.getName())
                + " (" + slot.label() + ").";
        }
        return "You equip the " + MarkupTag.ITEM.wrap(item.getName()) + " (" + slot.label() + ").";
    }

    static EquipmentSlot slotFor(Item item) {
        if (item instanceof Weapon) return EquipmentSlot.MAIN_HAND;
        if (item instanceof Shield) return EquipmentSlot.OFF_HAND;
        if (item instanceof Armor)  return EquipmentSlot.BODY;
        return null;
    }
}
