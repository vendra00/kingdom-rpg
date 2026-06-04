package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Equipment;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.item.enums.EquipmentSlot;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.PlayerRepository;

/**
 * Removes an item from an equipped slot, returning it to the inventory.
 * <p>Accepts either a slot name ({@code main hand}, {@code off hand}, {@code body})
 * or the equipped item's name as the argument.</p>
 */
@Component
@RequiredArgsConstructor
public class UnequipCommand implements Command {

    private final PlayerRepository playerRepository;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Unequip what?  (main hand · off hand · body — or the item name)";
        }

        String input = String.join(" ", args).toLowerCase();
        Equipment eq = player.getEquipment();

        // Match by slot name first
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.label().toLowerCase().equals(input)
                    || slot.name().toLowerCase().replace("_", " ").equals(input)) {
                return unequipSlot(player, eq, slot);
            }
        }

        // Partial slot name match
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.label().toLowerCase().contains(input)) {
                return unequipSlot(player, eq, slot);
            }
        }

        // Match by equipped item name
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item item = eq.getSlot(slot);
            if (item != null && item.getName().toLowerCase().contains(input)) {
                return unequipSlot(player, eq, slot);
            }
        }

        return "You don't have '" + input + "' equipped.";
    }

    private String unequipSlot(Player player, Equipment eq, EquipmentSlot slot) {
        Item item = eq.getSlot(slot);
        if (item == null) {
            return "Nothing is equipped in the " + slot.label() + " slot.";
        }
        eq.setSlot(slot, null);
        playerRepository.save(player);
        return "You unequip the " + MarkupTag.ITEM.wrap(item.getName()) + ".";
    }
}
