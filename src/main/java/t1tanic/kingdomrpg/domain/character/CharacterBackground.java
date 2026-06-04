package t1tanic.kingdomrpg.domain.character;

import java.util.Map;

import static t1tanic.kingdomrpg.domain.character.Attribute.*;

/**
 * Defines the historical background archetypes for characters and their associated starting attribute adjustments.
 * <p>Each background adds thematic depth to a character's history and grants a minor, targeted +1 attribute bonus
 * to reflect their past training, lifestyle, or social standing. It includes an exception-safe parsing strategy
 * with a standardized fallback system.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CharacterBackground {

    /**
     * Born to privilege, leadership, or high societal standing. Grants +1 Charisma.
     */
    NOBLE    (Map.of(CHARISMA,    1)),

    /**
     * Trained in physical warfare, tactical discipline, or guard duty. Grants +1 Strength.
     */
    SOLDIER  (Map.of(STRENGTH,    1)),

    /**
     * Dedicated to academic pursuits, deep research, or logical analysis. Grants +1 Intelligence.
     */
    SCHOLAR  (Map.of(INTELLIGENCE, 1)),

    /**
     * Raised in the wild fringes of civilization, accustomed to harsh survival. Grants +1 Constitution.
     */
    OUTLANDER(Map.of(CONSTITUTION, 1)),

    /**
     * Experienced in underworld dealings, stealth, or illicit operations. Grants +1 Dexterity.
     */
    CRIMINAL (Map.of(DEXTERITY,   1)),

    /**
     * Devoted to a religious order, temple service, or spiritual insight. Grants +1 Wisdom.
     */
    ACOLYTE  (Map.of(WISDOM,      1));

    private final Map<Attribute, Integer> bonuses;

    CharacterBackground(Map<Attribute, Integer> b) { this.bonuses = b; }

    /**
     * Retrieves the map of attribute type keys to their corresponding background bonus modifiers.
     *
     * @return an unmodifiable {@link Map} containing the attribute adjustments granted by this background
     */
    public Map<Attribute, Integer> getBonuses() { return bonuses; }

    /**
     * Safely resolves a string token into its matching {@code CharacterBackground} constant.
     * <p>This strategy normalizes the input to uppercase to match the enum definitions. If the string
     * is null or fails to match any existing background identifier, it catches the resulting
     * {@link IllegalArgumentException} and falls back gracefully to a default background selection.</p>
     *
     * @param s the raw text representation of the background to parse
     * @return the matching {@code CharacterBackground} constant, or {@code NOBLE} as the safety fallback choice
     */
    public static CharacterBackground fromString(String s) {
        if (s == null) return NOBLE;
        try { return valueOf(s.toUpperCase()); }
        catch (IllegalArgumentException e) { return NOBLE; }
    }
}
