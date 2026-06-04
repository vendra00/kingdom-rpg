package t1tanic.kingdomrpg.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.domain.CantripEffect;
import t1tanic.kingdomrpg.repository.CantripRepository;

import java.util.List;

import static t1tanic.kingdomrpg.domain.CantripEffect.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CantripInitializer {

    private final CantripRepository cantripRepository;

    public void seed() {
        log.debug("Seeding cantrips...");
        var cantrips = List.of(

            // ── Evocation (damage) ─────────────────────────────
            c("fire_bolt",    "Fire Bolt",       "Evocation",
              "Hurl a mote of fire. Deals 1d10 fire damage on a hit.",
              "mage", "fire", DAMAGE),
            c("ray_of_frost", "Ray of Frost",    "Evocation",
              "A frigid beam of blue-white light. Deals 1d8 cold damage and reduces the target's speed.",
              "mage", "cold", DAMAGE),
            c("shock_grasp",  "Shocking Grasp",  "Evocation",
              "Lightning springs from your hand. Deals 1d8 lightning damage; target can't take reactions.",
              "mage", "lightning", DAMAGE),
            c("sacred_flame", "Sacred Flame",    "Evocation",
              "Flame-like radiance descends on a creature. Deals 1d8 radiant damage.",
              "cleric,paladin", "radiant", DAMAGE),
            c("eldritch",     "Eldritch Blast",  "Evocation",
              "A beam of crackling force energy streaks toward a creature. Deals 1d10 force damage.",
              "mage,rogue", "force", DAMAGE),

            // ── Necromancy / Conjuration (damage / debuff) ─────
            c("toll_dead",    "Toll the Dead",   "Necromancy",
              "The sound of a doleful bell fills the air. Deals 1d8 necrotic damage (1d12 if already wounded).",
              "mage,cleric", "necrotic", DAMAGE),
            c("chill_touch",  "Chill Touch",     "Necromancy",
              "A ghostly hand chills the creature. Deals 1d8 necrotic damage and prevents the target from regaining HP.",
              "mage", "necrotic", DEBUFF),
            c("acid_splash",  "Acid Splash",     "Conjuration",
              "Hurl a bubble of acid. Deals 1d6 acid damage.",
              "mage,rogue", "acid", DAMAGE),
            c("poison_spray", "Poison Spray",    "Conjuration",
              "Project a cone of noxious gas. Deals 1d12 poison damage.",
              "mage,cleric,ranger", "poison", DAMAGE),

            // ── Transmutation / Enchantment (damage / debuff) ──
            c("thorn_whip",   "Thorn Whip",      "Transmutation",
              "A vine-like whip of thorns strikes a creature. Deals 1d6 piercing damage and pulls the target 10 feet closer.",
              "cleric,ranger", "piercing", DAMAGE),
            c("vicious_mock", "Vicious Mockery", "Enchantment",
              "Unleash a string of insults laced with subtle enchantments. Deals 1d4 psychic damage; target has disadvantage on its next attack.",
              "mage", "psychic", DEBUFF),

            // ── Utility ────────────────────────────────────────
            c("mage_hand",   "Mage Hand",        "Conjuration",
              "A spectral floating hand appears. You can use it to manipulate objects up to 30 feet away.",
              "mage,rogue", null, UTILITY),
            c("minor_illus", "Minor Illusion",   "Illusion",
              "Create a sound or an image of an object within range. Lasts 1 minute.",
              "mage,rogue", null, UTILITY),
            c("light",       "Light",            "Evocation",
              "Touch one object. It sheds bright light in a 20-foot radius and dim light in an additional 20 feet.",
              "mage,cleric,paladin", null, UTILITY),
            c("prestidig",   "Prestidigitation", "Transmutation",
              "Perform minor magical tricks: light candles, clean objects, create brief sensory effects.",
              "mage,rogue", null, UTILITY),

            // ── Buff / Support ─────────────────────────────────
            c("guidance",    "Guidance",         "Divination",
              "Touch a willing creature. Once before the spell ends it may add 1d4 to one ability check.",
              "cleric", null, BUFF),
            c("resistance",  "Resistance",       "Abjuration",
              "Touch a willing creature. Once before the spell ends it may add 1d4 to one saving throw.",
              "cleric,paladin", null, BUFF),
            c("blade_ward",  "Blade Ward",       "Abjuration",
              "Extend your hand and trace a sigil of warding. Until the end of your next turn you have resistance to bludgeoning, piercing, and slashing damage.",
              "warrior,paladin,rogue", null, BUFF),
            c("true_strike", "True Strike",      "Divination",
              "Gain brief insight into a target's defenses. You have advantage on your next attack roll against the creature.",
              "mage,rogue,ranger,warrior", null, BUFF),

            // ── Healing ────────────────────────────────────────
            c("spare_dying", "Spare the Dying",  "Necromancy",
              "Touch a living creature at 0 hit points. It becomes stable and ceases making death saving throws.",
              "cleric", null, HEALING)
        );
        cantripRepository.saveAll(cantrips);
        log.info("Seeded {} cantrips", cantrips.size());
    }

    private Cantrip c(String id, String name, String school, String desc,
                      String classes, String dmgType, CantripEffect effect) {
        return new Cantrip(id, name, school, desc, classes, dmgType, effect);
    }
}
