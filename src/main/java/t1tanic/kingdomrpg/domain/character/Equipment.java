package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.domain.item.enums.EquipmentSlot;

/**
 * Embeddable holding references to the three equippable item slots: main hand, off hand, and body.
 * Stored as foreign-key columns on the player table.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipped_main_hand_id")
    private Item mainHand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipped_off_hand_id")
    private Item offHand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipped_body_id")
    private Item body;

    public Item getSlot(EquipmentSlot slot) {
        return switch (slot) {
            case MAIN_HAND -> mainHand;
            case OFF_HAND  -> offHand;
            case BODY      -> body;
        };
    }

    public void setSlot(EquipmentSlot slot, Item item) {
        switch (slot) {
            case MAIN_HAND -> mainHand = item;
            case OFF_HAND  -> offHand  = item;
            case BODY      -> body     = item;
        }
    }

    /** Returns the slot this item occupies, or {@code null} if it is not equipped. */
    public EquipmentSlot slotOf(Item item) {
        if (item == null || item.getId() == null) return null;
        Long id = item.getId();
        if (mainHand != null && id.equals(mainHand.getId())) return EquipmentSlot.MAIN_HAND;
        if (offHand  != null && id.equals(offHand.getId()))  return EquipmentSlot.OFF_HAND;
        if (body     != null && id.equals(body.getId()))     return EquipmentSlot.BODY;
        return null;
    }

    public boolean isEquipped(Item item) {
        return slotOf(item) != null;
    }
}
