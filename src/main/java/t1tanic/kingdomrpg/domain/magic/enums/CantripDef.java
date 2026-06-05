package t1tanic.kingdomrpg.domain.magic.enums;

import t1tanic.kingdomrpg.domain.character.enums.CharacterClass;
import t1tanic.kingdomrpg.domain.magic.Cantrip;

import java.util.Arrays;
import java.util.stream.Collectors;

import static t1tanic.kingdomrpg.domain.character.enums.CharacterClass.*;
import static t1tanic.kingdomrpg.domain.magic.enums.CantripEffect.*;
import static t1tanic.kingdomrpg.domain.magic.enums.CantripSchool.*;
import static t1tanic.kingdomrpg.domain.magic.enums.DamageType.*;

/**
 * Canonical registry of every cantrip available in the game.
 * <p>Each constant is the single source of truth for a cantrip's id, name, school,
 * description, damage type, damage dice, effect category, and which classes may learn it.
 * {@link #toEntity()} converts a constant into the JPA entity used for persistence.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum CantripDef {

    // ── Evocation (damage) ───────────────────────────────────────────────────
    FIRE_BOLT(
            "fire_bolt", "Fire Bolt", EVOCATION,
            "Hurl a mote of fire. Deals 1d10 fire damage on a hit.",
            FIRE, "1d10", DAMAGE,
            MAGE),

    RAY_OF_FROST(
            "ray_of_frost", "Ray of Frost", EVOCATION,
            "A frigid beam of blue-white light. Deals 1d8 cold damage and reduces the target's speed.",
            COLD, "1d8", DAMAGE,
            MAGE),

    SHOCKING_GRASP(
            "shock_grasp", "Shocking Grasp", EVOCATION,
            "Lightning springs from your hand. Deals 1d8 lightning damage; target can't take reactions.",
            LIGHTNING, "1d8", DAMAGE,
            MAGE),

    SACRED_FLAME(
            "sacred_flame", "Sacred Flame", EVOCATION,
            "Flame-like radiance descends on a creature. Deals 1d8 radiant damage.",
            RADIANT, "1d8", DAMAGE,
            CLERIC, PALADIN),

    ELDRITCH_BLAST(
            "eldritch", "Eldritch Blast", EVOCATION,
            "A beam of crackling force energy streaks toward a creature. Deals 1d10 force damage.",
            FORCE, "1d10", DAMAGE,
            MAGE, ROGUE),

    LIGHT(
            "light", "Light", EVOCATION,
            "Touch one object. It sheds bright light in a 20-foot radius and dim light in an additional 20 feet.",
            null, null, UTILITY,
            MAGE, CLERIC, PALADIN),

    // ── Necromancy (damage / debuff / healing) ───────────────────────────────
    TOLL_THE_DEAD(
            "toll_dead", "Toll the Dead", NECROMANCY,
            "The sound of a doleful bell fills the air. Deals 1d8 necrotic damage (1d12 if already wounded).",
            NECROTIC, "1d8", DAMAGE,
            MAGE, CLERIC),

    CHILL_TOUCH(
            "chill_touch", "Chill Touch", NECROMANCY,
            "A ghostly hand chills the creature. Deals 1d8 necrotic damage and prevents the target from regaining HP.",
            NECROTIC, "1d8", DEBUFF,
            MAGE),

    SPARE_THE_DYING(
            "spare_dying", "Spare the Dying", NECROMANCY,
            "Touch a living creature at 0 hit points. It becomes stable and ceases making death saving throws.",
            null, null, HEALING,
            CLERIC),

    // ── Conjuration ──────────────────────────────────────────────────────────
    ACID_SPLASH(
            "acid_splash", "Acid Splash", CONJURATION,
            "Hurl a bubble of acid. Deals 1d6 acid damage.",
            ACID, "1d6", DAMAGE,
            MAGE, ROGUE),

    POISON_SPRAY(
            "poison_spray", "Poison Spray", CONJURATION,
            "Project a cone of noxious gas. Deals 1d12 poison damage.",
            POISON, "1d12", DAMAGE,
            MAGE, CLERIC, RANGER),

    MAGE_HAND(
            "mage_hand", "Mage Hand", CONJURATION,
            "A spectral floating hand appears. You can use it to manipulate objects up to 30 feet away.",
            null, null, UTILITY,
            MAGE, ROGUE),

    // ── Transmutation ────────────────────────────────────────────────────────
    THORN_WHIP(
            "thorn_whip", "Thorn Whip", TRANSMUTATION,
            "A vine-like whip of thorns strikes a creature. Deals 1d6 piercing damage and pulls the target 10 feet closer.",
            PIERCING, "1d6", DAMAGE,
            CLERIC, RANGER),

    PRESTIDIGITATION(
            "prestidig", "Prestidigitation", TRANSMUTATION,
            "Perform minor magical tricks: light candles, clean objects, create brief sensory effects.",
            null, null, UTILITY,
            MAGE, ROGUE),

    // ── Enchantment ──────────────────────────────────────────────────────────
    VICIOUS_MOCKERY(
            "vicious_mock", "Vicious Mockery", ENCHANTMENT,
            "Unleash a string of insults laced with subtle enchantments. Deals 1d4 psychic damage; target has disadvantage on its next attack.",
            PSYCHIC, "1d4", DEBUFF,
            MAGE),

    // ── Illusion ─────────────────────────────────────────────────────────────
    MINOR_ILLUSION(
            "minor_illus", "Minor Illusion", ILLUSION,
            "Create a sound or an image of an object within range. Lasts 1 minute.",
            null, null, UTILITY,
            MAGE, ROGUE),

    // ── Divination ───────────────────────────────────────────────────────────
    GUIDANCE(
            "guidance", "Guidance", DIVINATION,
            "Touch a willing creature. Once before the spell ends it may add 1d4 to one ability check.",
            null, null, BUFF,
            CLERIC),

    TRUE_STRIKE(
            "true_strike", "True Strike", DIVINATION,
            "Gain brief insight into a target's defenses. You have advantage on your next attack roll against the creature.",
            null, null, BUFF,
            MAGE, ROGUE, RANGER, WARRIOR),

    // ── Abjuration ───────────────────────────────────────────────────────────
    RESISTANCE(
            "resistance", "Resistance", ABJURATION,
            "Touch a willing creature. Once before the spell ends it may add 1d4 to one saving throw.",
            null, null, BUFF,
            CLERIC, PALADIN),

    BLADE_WARD(
            "blade_ward", "Blade Ward", ABJURATION,
            "Extend your hand and trace a sigil of warding. Until the end of your next turn you have resistance to bludgeoning, piercing, and slashing damage.",
            null, null, BUFF,
            WARRIOR, PALADIN, ROGUE);

    // ─────────────────────────────────────────────────────────────────────────

    private final String          id;
    private final String          displayName;
    private final CantripSchool   school;
    private final String          description;
    private final DamageType      damageType;
    private final String          damageDice;
    private final CantripEffect   effect;
    private final CharacterClass[] allowedClasses;

    CantripDef(String id, String displayName, CantripSchool school, String description,
               DamageType damageType, String damageDice, CantripEffect effect,
               CharacterClass... allowedClasses) {
        this.id             = id;
        this.displayName    = displayName;
        this.school         = school;
        this.description    = description;
        this.damageType     = damageType;
        this.damageDice     = damageDice;
        this.effect         = effect;
        this.allowedClasses = allowedClasses;
    }

    public String           id()             { return id; }
    public String           displayName()    { return displayName; }
    public CantripSchool    school()         { return school; }
    public String           description()    { return description; }
    public DamageType       damageType()     { return damageType; }
    public String           damageDice()     { return damageDice; }
    public CantripEffect    effect()         { return effect; }
    public CharacterClass[] allowedClasses() { return allowedClasses; }

    /** Converts this definition into the JPA entity ready for persistence. */
    public Cantrip toEntity() {
        String dmgLabel  = damageType != null ? damageType.label() : null;
        String classesStr = Arrays.stream(allowedClasses)
                                  .map(CharacterClass::id)
                                  .collect(Collectors.joining(","));
        return new Cantrip(id, displayName, school.displayName(), description,
                           classesStr, dmgLabel, damageDice, effect);
    }
}
