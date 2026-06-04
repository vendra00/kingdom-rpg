package t1tanic.kingdomrpg.domain.character.enums;

/**
 * Groups related specialized skills into broad ability categories and binds them
 * to an overarching core attribute.
 * <p>These categories represent the main skill groups (e.g., {@code STEALTH}, {@code ATHLETICS})
 * used to classify actions. The associated {@link Attribute} determines which raw capability modifier
 * applies when calculating success probabilities during a resolution roll.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum AbilityCategory {

    /**
     * Governs interpersonal dialogue, deception, and coercion. Dependent on raw force of personality.
     */
    PERSUASION("Persuasion", Attribute.CHARISMA),

    /**
     * Governs environmental awareness, physical tracking, and reading intentions. Dependent on intuition.
     */
    PERCEPTION("Perception", Attribute.WISDOM),

    /**
     * Governs raw muscular exertion tasks such as scaling, jumping, or swimming. Dependent on physical power.
     */
    ATHLETICS( "Athletics",  Attribute.STRENGTH),

    /**
     * Governs moving silently, hiding, or sleight of hand. Dependent on fine motor skills and agility.
     */
    STEALTH(   "Stealth",    Attribute.DEXTERITY),

    /**
     * Governs information recall, investigation parsing, and item appraisal. Dependent on analytical acuity.
     */
    KNOWLEDGE( "Knowledge",  Attribute.INTELLIGENCE),

    /**
     * Governs outdoor navigation, foraging resources, and tracking trails. Dependent on practical insight.
     */
    SURVIVAL(  "Survival",   Attribute.WISDOM),

    /**
     * Governs dynamic balance configurations, tumbling rolls, and dodging hazards. Dependent on reflexes.
     */
    ACROBATICS("Acrobatics", Attribute.DEXTERITY);

    private final String    displayName;
    private final Attribute attribute;

    AbilityCategory(String displayName, Attribute attribute) {
        this.displayName = displayName;
        this.attribute   = attribute;
    }

    /**
     * @return the human-readable string title used for client interfaces or UI layouts
     */
    public String    displayName() { return displayName; }
    /**
     * @return the scaling core {@link Attribute} governing this skill category
     */
    public Attribute attribute()   { return attribute; }
}
