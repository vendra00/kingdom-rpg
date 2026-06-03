package t1tanic.kingdomrpg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.repository.CantripRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CantripInitializer {

    private final CantripRepository cantripRepository;

    public void seed() {
        cantripRepository.saveAll(List.of(

            // ── Evocation (damage) ─────────────────────────────
            c("fire_bolt",    "Fire Bolt",       "Evocation",
              "Hurl a mote of fire. Deals 1d10 fire damage on a hit.",
              "mage", "fire", "damage"),
            c("ray_of_frost", "Ray of Frost",    "Evocation",
              "A frigid beam of blue-white light. Deals 1d8 cold damage and reduces the target's speed.",
              "mage", "cold", "damage"),
            c("shock_grasp",  "Shocking Grasp",  "Evocation",
              "Lightning springs from your hand. Deals 1d8 lightning damage; target can't take reactions.",
              "mage", "lightning", "damage"),
            c("sacred_flame", "Sacred Flame",    "Evocation",
              "Flame-like radiance descends on a creature. Deals 1d8 radiant damage.",
              "cleric,paladin", "radiant", "damage"),
            c("eldritch",     "Eldritch Blast",  "Evocation",
              "A beam of crackling force energy streaks toward a creature. Deals 1d10 force damage.",
              "mage,rogue", "force", "damage"),

            // ── Necromancy / Conjuration (damage / debuff) ─────
            c("toll_dead",    "Toll the Dead",   "Necromancy",
              "The sound of a doleful bell fills the air. Deals 1d8 necrotic damage (1d12 if already wounded).",
              "mage,cleric", "necrotic", "damage"),
            c("chill_touch",  "Chill Touch",     "Necromancy",
              "A ghostly hand chills the creature. Deals 1d8 necrotic damage and prevents the target from regaining HP.",
              "mage", "necrotic", "debuff"),
            c("acid_splash",  "Acid Splash",     "Conjuration",
              "Hurl a bubble of acid. Deals 1d6 acid damage.",
              "mage,rogue", "acid", "damage"),
            c("poison_spray", "Poison Spray",    "Conjuration",
              "Project a cone of noxious gas. Deals 1d12 poison damage.",
              "mage,cleric,ranger", "poison", "damage"),

            // ── Transmutation / Enchantment (damage / debuff) ──
            c("thorn_whip",   "Thorn Whip",      "Transmutation",
              "A vine-like whip of thorns strikes a creature. Deals 1d6 piercing damage and pulls the target 10 feet closer.",
              "cleric,ranger", "piercing", "damage"),
            c("vicious_mock", "Vicious Mockery", "Enchantment",
              "Unleash a string of insults laced with subtle enchantments. Deals 1d4 psychic damage; target has disadvantage on its next attack.",
              "mage", "psychic", "debuff"),

            // ── Utility ────────────────────────────────────────
            c("mage_hand",   "Mage Hand",        "Conjuration",
              "A spectral floating hand appears. You can use it to manipulate objects up to 30 feet away.",
              "mage,rogue", null, "utility"),
            c("minor_illus", "Minor Illusion",   "Illusion",
              "Create a sound or an image of an object within range. Lasts 1 minute.",
              "mage,rogue", null, "utility"),
            c("light",       "Light",            "Evocation",
              "Touch one object. It sheds bright light in a 20-foot radius and dim light in an additional 20 feet.",
              "mage,cleric,paladin", null, "utility"),
            c("prestidig",   "Prestidigitation", "Transmutation",
              "Perform minor magical tricks: light candles, clean objects, create brief sensory effects.",
              "mage,rogue", null, "utility"),

            // ── Buff / Support ─────────────────────────────────
            c("guidance",    "Guidance",         "Divination",
              "Touch a willing creature. Once before the spell ends it may add 1d4 to one ability check.",
              "cleric", null, "buff"),
            c("resistance",  "Resistance",       "Abjuration",
              "Touch a willing creature. Once before the spell ends it may add 1d4 to one saving throw.",
              "cleric,paladin", null, "buff"),
            c("blade_ward",  "Blade Ward",       "Abjuration",
              "Extend your hand and trace a sigil of warding. Until the end of your next turn you have resistance to bludgeoning, piercing, and slashing damage.",
              "warrior,paladin,rogue", null, "buff"),
            c("true_strike", "True Strike",      "Divination",
              "Gain brief insight into a target's defenses. You have advantage on your next attack roll against the creature.",
              "mage,rogue,ranger,warrior", null, "buff"),

            // ── Healing ────────────────────────────────────────
            c("spare_dying", "Spare the Dying",  "Necromancy",
              "Touch a living creature at 0 hit points. It becomes stable and ceases making death saving throws.",
              "cleric", null, "healing")
        ));
    }

    private Cantrip c(String id, String name, String school, String desc,
                      String classes, String dmgType, String effect) {
        return new Cantrip(id, name, school, desc, classes, dmgType, effect);
    }
}
