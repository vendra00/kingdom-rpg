package t1tanic.kingdomrpg.domain.magic;

/**
 * Defines the functional classifications of cantrips and manages their corresponding casting narrative templates.
 * <p>Each effect type dictates the mechanical nature of a minor spell (such as dealing damage, applying a penalty,
 * or mending wounds) and provides a localized format string to generate immersion text during game loop
 * execution events.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CantripEffect {
    /**
     * Direct offensive spells meant to lower a target's health pool.
     */
    DAMAGE ("You focus your energy and shape the arcane threads of %s..."),
    /**
     * Hindering spells designed to weaken, slow, or penalize a target's attributes.
     */
    DEBUFF ("Dark energy coils around your fingers as you invoke %s..."),
    /**
     * Enhancing spells designed to bolster, shield, or augment an ally's capabilities.
     */
    BUFF   ("A warm shimmer envelops your hands as you channel %s..."),
    /**
     * Non-combat, sensory, or environmental spells used to interact with world mechanics.
     */
    UTILITY("You weave the subtle patterns of %s..."),
    /**
     * Restorative spells aimed at replenishing vital pools or healing wounds.
     */
    HEALING("Soft light flows through you as you call upon %s...");

    private final String channelFmt;

    CantripEffect(String channelFmt) {
        this.channelFmt = channelFmt;
    }

    /**
     * Formats and returns the casting flavor text for an active gameplay action message log.
     * <p>Injects the human-readable name of the specific cantrip into the active template configuration.</p>
     *
     * @param spellName the display name of the cantrip being cast (e.g., "Fire Bolt")
     * @return the formatted narrative {@link String} describing the activation process
     */
    public String channel(String spellName) {
        return channelFmt.formatted(spellName);
    }
}
