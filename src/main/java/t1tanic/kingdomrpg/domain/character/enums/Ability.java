package t1tanic.kingdomrpg.domain.character.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static t1tanic.kingdomrpg.domain.character.enums.AbilityCategory.*;
import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Enumerates all active skill-check abilities available to characters within the RPG system.
 * <p>Each ability declares one or more contributing {@link Attribute}s. During resolution the
 * highest modifier among them is used, rewarding specialised builds without hard-locking a single stat.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum Ability {

    // ── Persuasion ────────────────────────────────────────────────────────────
    BRIBE("bribe", "Bribe", PERSUASION, attrs(CHARISMA), 14,
          "Offer coin or goods to sway someone's loyalty.",
          "The offer proves irresistible.  A knowing nod, a quiet exchange — done.",
          "Your coin is refused with a cold stare.  'I can't be bought.'"),

    /** Physical menace (STR) or sheer force of personality (CHA) — highest modifier applies. */
    INTIMIDATE("intimidate", "Intimidate", PERSUASION, attrs(STRENGTH, CHARISMA), 12,
               "Use menace and force of will to instill fear.",
               "Your voice drops to a dangerous edge.  They back down, unwilling to test you.",
               "Your display falls flat.  They meet your gaze without flinching."),

    /** Reasoned argument driven by charm (CHA) and sharp logic (INT). */
    CONVINCE("convince", "Convince", PERSUASION, attrs(CHARISMA, INTELLIGENCE), 14,
             "Argue your case with reason and charm.",
             "Your argument is clear and compelling.  Doubt turns to agreement.",
             "Your words land awkwardly.  They remain unconvinced and unmoved."),

    /** A plausible lie needs both believability (CHA) and quick thinking (INT). */
    DECEIVE("deceive", "Deceive", PERSUASION, attrs(CHARISMA, INTELLIGENCE), 14,
            "Mislead someone with a plausible lie.",
            "You weave the lie with just enough truth.  They believe every word.",
            "A flicker in your eyes gives you away.  'You're lying,' they say flatly."),

    /** Deal-making blends charm (CHA) with calculating intelligence (INT). */
    NEGOTIATE("negotiate", "Negotiate", PERSUASION, attrs(CHARISMA, INTELLIGENCE), 11,
              "Work toward a mutually beneficial arrangement.",
              "Both sides find common ground.  A fair deal is struck.",
              "Your proposed terms are dismissed.  They're not interested in compromise."),

    // ── Perception ───────────────────────────────────────────────────────────
    SURVEY("survey", "Survey", PERCEPTION, attrs(WISDOM), 12,
           "Scan your surroundings for unusual details.",
           "Your eyes sweep the area with practiced focus.  Something the others missed.",
           "Nothing catches your attention.  If there's something here, it stays hidden."),

    LISTEN("listen", "Listen", PERCEPTION, attrs(WISDOM), 10,
           "Focus your hearing for nearby sounds or voices.",
           "You hold your breath.  Through the silence, a distant sound becomes clear.",
           "You strain your ears but hear nothing useful."),

    SENSE_MOTIVE("sense", "Sense Motive", PERCEPTION, attrs(WISDOM), 14,
                 "Read the true intentions behind someone's words.",
                 "You study their face carefully.  Beneath the words, you read the truth.",
                 "Their expression reveals nothing.  You can't tell what they really mean."),

    // ── Athletics ─────────────────────────────────────────────────────────────
    JUMP("jump", "Jump", ATHLETICS, attrs(STRENGTH), 12,
         "Leap across a gap or over an obstacle.",
         "You time your leap perfectly and clear the obstacle with ease.",
         "You misjudge the distance and stumble, barely catching yourself."),

    /** Brute pull strength (STR) and body coordination (DEX) both contribute. */
    CLIMB("climb", "Climb", ATHLETICS, attrs(STRENGTH, DEXTERITY), 14,
          "Scale a wall, cliff, or other vertical surface.",
          "Your hands find every grip.  You pull yourself up without difficulty.",
          "Halfway up, your footing slips.  You slide back down, palms stinging."),

    /** Power through water with raw strength (STR) sustained by endurance (CON). */
    SWIM("swim", "Swim", ATHLETICS, attrs(STRENGTH, CONSTITUTION), 11,
         "Propel yourself through water.",
         "Your strokes are strong and even.  You cut through the water without effort.",
         "You fight the current but make little headway, exhausted."),

    SHOVE("shove", "Shove", ATHLETICS, attrs(STRENGTH), 13,
          "Forcefully push an obstacle or creature aside.",
          "You drive forward with your shoulder.  It gives way with a satisfying crash.",
          "You push with all your might, but it doesn't budge an inch."),

    // ── Stealth ───────────────────────────────────────────────────────────────
    HIDE("hide", "Hide", STEALTH, attrs(DEXTERITY), 13,
         "Conceal yourself in shadows or behind cover.",
         "You melt into the shadows.  Your breathing slows.  No one would know you're there.",
         "A creak of the floor or a shaft of light gives you away."),

    SNEAK("sneak", "Sneak", STEALTH, attrs(DEXTERITY), 12,
          "Move through an area without making noise.",
          "Each footfall is placed with precision.  You pass through like a ghost.",
          "A pebble, a creak, an unlucky breath — something betrays your presence."),

    /** Precise fingers (DEX) guided by a quick mind (INT) — both matter. */
    PICKPOCKET("pickpocket", "Pickpocket", STEALTH, attrs(DEXTERITY, INTELLIGENCE), 16,
               "Quietly lift a small item from a person or container.",
               "Light fingers, a moment of distraction.  It's done before they notice.",
               "Your hand grazes the target too conspicuously.  They pull back with a sharp look."),

    // ── Knowledge ─────────────────────────────────────────────────────────────
    RECALL("recall", "Recall Lore", KNOWLEDGE, attrs(INTELLIGENCE), 13,
           "Draw upon memory of history, arcana, or lore.",
           "The memory surfaces clearly — a passage once read, a detail once forgotten.",
           "The knowledge is on the tip of your tongue, but you can't quite grasp it."),

    /** Examination uses scholarly knowledge (INT) as well as keen observation (WIS). */
    IDENTIFY("identify", "Identify", KNOWLEDGE, attrs(INTELLIGENCE, WISDOM), 14,
             "Examine an unknown object to determine its nature.",
             "You turn it over in your hands, tracing its markings.  Its nature becomes apparent.",
             "Despite careful examination, you can't determine what this thing is or does."),

    /** Pattern-finding is analytical (INT) and perceptual (WIS) in equal measure. */
    INVESTIGATE("investigate", "Investigate", KNOWLEDGE, attrs(INTELLIGENCE, WISDOM), 12,
                "Analyze clues and evidence for deeper meaning.",
                "You piece the clues together.  A pattern emerges that wasn't obvious before.",
                "You look carefully but see nothing you didn't already know."),

    // ── Survival ─────────────────────────────────────────────────────────────
    /** Knowing where to look (WIS) and enduring the search (CON) — best of both. */
    FORAGE("forage", "Forage", SURVIVAL, attrs(WISDOM, CONSTITUTION), 12,
           "Search the environment for food, water, or herbs.",
           "You know where to look.  Roots, berries, a hidden spring — the land provides.",
           "After some time searching, you find nothing edible or useful."),

    TRACK("track", "Track", SURVIVAL, attrs(WISDOM), 14,
          "Follow the trail of a creature or person.",
          "The signs are subtle but readable — broken stems, disturbed earth.  You have the trail.",
          "The tracks vanish or become too muddled to follow reliably."),

    /** Reading the land (WIS) and reasoning through bearings (INT). */
    NAVIGATE("navigate", "Navigate", SURVIVAL, attrs(WISDOM, INTELLIGENCE), 11,
             "Determine your bearings and find the right path.",
             "You read the landscape, the light, the stars.  Direction reasserts itself.",
             "You're not sure which way is which.  The path remains unclear."),

    // ── Acrobatics ────────────────────────────────────────────────────────────
    BALANCE("balance", "Balance", ACROBATICS, attrs(DEXTERITY), 11,
            "Maintain footing on an unstable or narrow surface.",
            "Arms out, weight centered — you move across without wavering.",
            "Your footing shifts at the wrong moment.  You nearly topple before catching yourself."),

    TUMBLE("tumble", "Tumble", ACROBATICS, attrs(DEXTERITY), 13,
           "Roll or dodge past an obstacle or hazard.",
           "You tuck and roll, absorbing the impact and coming up smoothly on the other side.",
           "Your timing is off.  You land hard and inelegantly."),

    DODGE("dodge", "Dodge", ACROBATICS, attrs(DEXTERITY), 14,
          "Evade an incoming strike or environmental hazard.",
          "You read the hazard a split second before it arrives and step clear.",
          "You move too late.  The impact grazes you before you get clear.");

    // ─────────────────────────────────────────────────────────────────────────

    /** Abilities triggered implicitly by the LLM during talk — not available via explicit attempt. */
    private static final Set<String> CONVERSATION_ONLY = Set.of(
            "bribe", "intimidate", "convince", "deceive", "negotiate", "sense"
    );

    private final String          id;
    private final String          displayName;
    private final AbilityCategory category;
    private final Attribute[]     attributes;
    private final int             dc;
    private final String          description;
    private final String          successMsg;
    private final String          failureMsg;

    Ability(String id, String displayName, AbilityCategory category, Attribute[] attributes, int dc,
            String description, String successMsg, String failureMsg) {
        this.id          = id;
        this.displayName = displayName;
        this.category    = category;
        this.attributes  = attributes;
        this.dc          = dc;
        this.description = description;
        this.successMsg  = successMsg;
        this.failureMsg  = failureMsg;
    }

    public boolean        isConversationOnly() { return CONVERSATION_ONLY.contains(id); }
    public String         id()          { return id; }
    public String         displayName() { return displayName; }
    public String         description() { return description; }
    public int            dc()          { return dc; }
    public AbilityCategory category()   { return category; }
    public Attribute[]    attributes()  { return attributes; }
    public String         successMsg()  { return successMsg; }
    public String         failureMsg()  { return failureMsg; }

    public static List<Ability> byCategory(AbilityCategory cat) {
        return Arrays.stream(values()).filter(a -> a.category == cat).toList();
    }

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

    private static Attribute[] attrs(Attribute... a) { return a; }
}
