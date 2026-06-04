package t1tanic.kingdomrpg.domain.item;

public enum ItemTag {

    CONSUMABLE("Consumable"),
    EQUIPMENT ("Equipment"),
    KEY_ITEM  ("Key Item"),
    MATERIAL  ("Material"),
    MISC      ("Misc");

    private final String label;

    ItemTag(String label) { this.label = label; }

    public String label() { return label; }
}
