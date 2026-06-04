package t1tanic.kingdomrpg.domain.character;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static t1tanic.kingdomrpg.domain.character.AbilityCategory.*;

/**
 * Enumerates all active skill-check abilities available to characters within the RPG system.
 * <p>Each enum constant maps to a specific action (e.g., {@code SNEAK}, {@code RECALL}) categorized
 * under a broader {@link AbilityCategory}. These abilities possess a baseline Difficulty Class (DC)
 * used to resolve skill checks, along with descriptive narrative text for both successful and
 * failed execution attempts.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum Ability {

    // ── Persuasion ────────────────────────────────────────────────────────────
    /**
     * Offer coin or goods to sway someone's loyalty.
     */
    BRIBE("bribe", "Bribe", PERSUASION, 14,
          "Offer coin or goods to sway someone's loyalty.",
          "The offer proves irresistible.  A knowing nod, a quiet exchange — done.",
          "Your coin is refused with a cold stare.  'I can't be bought.'"),

    /**
     * Use menace and force of will to instill fear.
     */
    INTIMIDATE("intimidate", "Intimidate", PERSUASION, 12,
               "Use menace and force of will to instill fear.",
               "Your voice drops to a dangerous edge.  They back down, unwilling to test you.",
               "Your display falls flat.  They meet your gaze without flinching."),

    /**
     * Argue your case with reason and charm.
     */
    CONVINCE("convince", "Convince", PERSUASION, 14,
             "Argue your case with reason and charm.",
             "Your argument is clear and compelling.  Doubt turns to agreement.",
             "Your words land awkwardly.  They remain unconvinced and unmoved."),

    /**
     * Mislead someone with a plausible lie.
     */
    DECEIVE("deceive", "Deceive", PERSUASION, 14,
            "Mislead someone with a plausible lie.",
            "You weave the lie with just enough truth.  They believe every word.",
            "A flicker in your eyes gives you away.  'You're lying,' they say flatly."),

    /**
     * Work toward a mutually beneficial arrangement.
     */
    NEGOTIATE("negotiate", "Negotiate", PERSUASION, 11,
              "Work toward a mutually beneficial arrangement.",
              "Both sides find common ground.  A fair deal is struck.",
              "Your proposed terms are dismissed.  They're not interested in compromise."),

    // ── Perception ───────────────────────────────────────────────────────────
    /**
     * Scan your surroundings for unusual details.
     */
    SURVEY("survey", "Survey", PERCEPTION, 12,
           "Scan your surroundings for unusual details.",
           "Your eyes sweep the area with practiced focus.  Something the others missed.",
           "Nothing catches your attention.  If there's something here, it stays hidden."),

    /**
     * Focus your hearing for nearby sounds or voices.
     */
    LISTEN("listen", "Listen", PERCEPTION, 10,
           "Focus your hearing for nearby sounds or voices.",
           "You hold your breath.  Through the silence, a distant sound becomes clear.",
           "You strain your ears but hear nothing useful."),

    /**
     * Methodically check an area for hidden objects.
     */
    SEARCH("search", "Search", PERCEPTION, 13,
           "Methodically check an area for hidden objects.",
           "You work systematically through the area.  Your diligence is rewarded.",
           "You come up empty.  Either there's nothing here, or it's very well hidden."),

    /**
     * Read the true intentions behind someone's words.
     */
    SENSE_MOTIVE("sense", "Sense Motive", PERCEPTION, 14,
                 "Read the true intentions behind someone's words.",
                 "You study their face carefully.  Beneath the words, you read the truth.",
                 "Their expression reveals nothing.  You can't tell what they really mean."),

    // ── Athletics ─────────────────────────────────────────────────────────────
    /**
     * Leap across a gap or over an obstacle.
     */
    JUMP("jump", "Jump", ATHLETICS, 12,
         "Leap across a gap or over an obstacle.",
         "You time your leap perfectly and clear the obstacle with ease.",
         "You misjudge the distance and stumble, barely catching yourself."),

    /**
     * Scale a wall, cliff, or other vertical surface.
     */
    CLIMB("climb", "Climb", ATHLETICS, 14,
          "Scale a wall, cliff, or other vertical surface.",
          "Your hands find every grip.  You pull yourself up without difficulty.",
          "Halfway up, your footing slips.  You slide back down, palms stinging."),

    /**
     * Propel yourself through water.
     */
    SWIM("swim", "Swim", ATHLETICS, 11,
         "Propel yourself through water.",
         "Your strokes are strong and even.  You cut through the water without effort.",
         "You fight the current but make little headway, exhausted."),

    /**
     * Forcefully push an obstacle or creature aside.
     */
    SHOVE("shove", "Shove", ATHLETICS, 13,
          "Forcefully push an obstacle or creature aside.",
          "You drive forward with your shoulder.  It gives way with a satisfying crash.",
          "You push with all your might, but it doesn't budge an inch."),

    // ── Stealth ───────────────────────────────────────────────────────────────
    /**
     * Conceal yourself in shadows or behind cover.
     */
    HIDE("hide", "Hide", STEALTH, 13,
         "Conceal yourself in shadows or behind cover.",
         "You melt into the shadows.  Your breathing slows.  No one would know you're there.",
         "A creak of the floor or a shaft of light gives you away."),

    /**
     * Move through an area without making noise.
     */
    SNEAK("sneak", "Sneak", STEALTH, 12,
          "Move through an area without making noise.",
          "Each footfall is placed with precision.  You pass through like a ghost.",
          "A pebble, a creak, an unlucky breath — something betrays your presence."),

    /**
     * Quietly lift a small item from a person or container.
     */
    PICKPOCKET("pickpocket", "Pickpocket", STEALTH, 16,
               "Quietly lift a small item from a person or container.",
               "Light fingers, a moment of distraction.  It's done before they notice.",
               "Your hand grazes the target too conspicuously.  They pull back with a sharp look."),

    // ── Knowledge ─────────────────────────────────────────────────────────────
    /**
     * Draw upon memory of history, arcana, or lore.
     */
    RECALL("recall", "Recall Lore", KNOWLEDGE, 13,
           "Draw upon memory of history, arcana, or lore.",
           "The memory surfaces clearly — a passage once read, a detail once forgotten.",
           "The knowledge is on the tip of your tongue, but you can't quite grasp it."),

    /**
     * Examine an unknown object to determine its nature.
     */
    IDENTIFY("identify", "Identify", KNOWLEDGE, 14,
             "Examine an unknown object to determine its nature.",
             "You turn it over in your hands, tracing its markings.  Its nature becomes apparent.",
             "Despite careful examination, you can't determine what this thing is or does."),

    /**
     * Analyze clues and evidence for deeper meaning.
     */
    INVESTIGATE("investigate", "Investigate", KNOWLEDGE, 12,
                "Analyze clues and evidence for deeper meaning.",
                "You piece the clues together.  A pattern emerges that wasn't obvious before.",
                "You look carefully but see nothing you didn't already know."),

    // ── Survival ─────────────────────────────────────────────────────────────
    /**
     * Search the environment for food, water, or herbs.
     */
    FORAGE("forage", "Forage", SURVIVAL, 12,
           "Search the environment for food, water, or herbs.",
           "You know where to look.  Roots, berries, a hidden spring — the land provides.",
           "After some time searching, you find nothing edible or useful."),

    /**
     * Follow the trail of a creature or person.
     */
    TRACK("track", "Track", SURVIVAL, 14,
          "Follow the trail of a creature or person.",
          "The signs are subtle but readable — broken stems, disturbed earth.  You have the trail.",
          "The tracks vanish or become too muddled to follow reliably."),

    /**
     * Determine your bearings and find the right path.
     */
    NAVIGATE("navigate", "Navigate", SURVIVAL, 11,
             "Determine your bearings and find the right path.",
             "You read the landscape, the light, the stars.  Direction reasserts itself.",
             "You're not sure which way is which.  The path remains unclear."),

    // ── Acrobatics ────────────────────────────────────────────────────────────
    /**
     * Maintain footing on an unstable or narrow surface.
     */
    BALANCE("balance", "Balance", ACROBATICS, 11,
            "Maintain footing on an unstable or narrow surface.",
            "Arms out, weight centered — you move across without wavering.",
            "Your footing shifts at the wrong moment.  You nearly topple before catching yourself."),

    /**
     * Roll or dodge past an obstacle or hazard.
     */
    TUMBLE("tumble", "Tumble", ACROBATICS, 13,
           "Roll or dodge past an obstacle or hazard.",
           "You tuck and roll, absorbing the impact and coming up smoothly on the other side.",
           "Your timing is off.  You land hard and inelegantly."),

    /**
     * Evade an incoming strike or environmental hazard.
     */
    DODGE("dodge", "Dodge", ACROBATICS, 14,
          "Evade an incoming strike or environmental hazard.",
          "You read the hazard a split second before it arrives and step clear.",
          "You move too late.  The impact grazes you before you get clear.");

    // ─────────────────────────────────────────────────────────────────────────

    private final String          id;
    private final String          displayName;
    private final AbilityCategory category;
    private final int             dc;
    private final String          description;
    private final String          successMsg;
    private final String          failureMsg;

    Ability(String id, String displayName, AbilityCategory category, int dc,
            String description, String successMsg, String failureMsg) {
        this.id          = id;
        this.displayName = displayName;
        this.category    = category;
        this.dc          = dc;
        this.description = description;
        this.successMsg  = successMsg;
        this.failureMsg  = failureMsg;
    }

    /**
     * @return the machine-readable lowercase string unique identifier
     */
    public String id() { return id; }

    /**
     * @return the human-readable string title used for frontend UI layouts
     */
    public String displayName() { return displayName; }

    /**
     * @return the high-level {@link AbilityCategory} grouping this item belongs to
     */
    public String description() { return description; }

    /**
     * @return the baseline Difficulty Class threshold score required to pass this action check
     */
    public int dc() { return dc; }

    /**
     * @return the flavor text description outlining what the ability does
     */
    public AbilityCategory category() { return category; }

    /**
     * @return the narrative flavor string displayed to the user upon a successful check roll
     */
    public String successMsg() { return successMsg; }

    /**
     * @return the narrative flavor string displayed to the user upon a failed check roll
     */
    public String failureMsg() { return failureMsg; }

    /**
     * Filters and collects all abilities belonging to a specific category.
     *
     * @param cat the target {@link AbilityCategory} filter
     * @return an unmodifiable {@link List} of matching {@code Ability} constants in declaration order
     */
    public static List<Ability> byCategory(AbilityCategory cat) {
        return Arrays.stream(values()).filter(a -> a.category == cat).toList();
    }

    /**
     * Attempts to resolve a string input into a matching {@code Ability} instance.
     * <p>The search checks elements in two consecutive phases:</p>
     * <ol>
     * <li>Exact match comparison against the machine-readable lowercase {@code id}.</li>
     * <li>Case-insensitive partial matching containment inside the {@code displayName}.</li>
     * </ol>
     *
     * @param input the raw string token provided by user execution or parsing systems
     * @return an {@link Optional} containing the matched {@code Ability}, or {@link Optional#empty()} if no match found
     */
    public static Optional<Ability> fromInput(String input) {
        String lower = input.trim().toLowerCase();
        for (Ability a : values()) {
            if (a.id.equals(lower)) return Optional.of(a);
        }
        for (Ability a : values()) {
            if (a.displayName.toLowerCase().contains(lower)) return Optional.of(a);
        }
        return Optional.empty();
    }
}
