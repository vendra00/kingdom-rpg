package t1tanic.kingdomrpg.domain.item.enums;

/**
 * Defines the valid body slots a player can equip items into.
 */
public enum EquipmentSlot {
    MAIN_HAND("Main Hand"),
    OFF_HAND ("Off Hand"),
    BODY     ("Body");

    private final String label;

    EquipmentSlot(String label) { this.label = label; }

    public String label() { return label; }
}
