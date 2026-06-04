package t1tanic.kingdomrpg.domain;

public enum AbilityCategory {

    PERSUASION("Persuasion", Attribute.CHARISMA),
    PERCEPTION("Perception", Attribute.WISDOM),
    ATHLETICS( "Athletics",  Attribute.STRENGTH),
    STEALTH(   "Stealth",    Attribute.DEXTERITY),
    KNOWLEDGE( "Knowledge",  Attribute.INTELLIGENCE),
    SURVIVAL(  "Survival",   Attribute.WISDOM),
    ACROBATICS("Acrobatics", Attribute.DEXTERITY);

    private final String    displayName;
    private final Attribute attribute;

    AbilityCategory(String displayName, Attribute attribute) {
        this.displayName = displayName;
        this.attribute   = attribute;
    }

    public String    displayName() { return displayName; }
    public Attribute attribute()   { return attribute; }
}
