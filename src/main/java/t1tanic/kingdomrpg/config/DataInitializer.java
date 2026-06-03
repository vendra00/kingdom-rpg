package t1tanic.kingdomrpg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.*;
import t1tanic.kingdomrpg.repository.*;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository    roomRepository;
    private final ItemRepository    itemRepository;
    private final CantripRepository cantripRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedWorld() {
        if (roomRepository.count() > 0) return;

        // ── Rooms ──────────────────────────────────────────────
        Room entrance = room("Castle Entrance",
            "You stand at the imposing gates of an ancient castle. Stone walls rise around you, " +
            "covered in moss and ivy. A torchlit corridor stretches to the north.");
        Room hallway = room("Great Hallway",
            "A long, dimly lit hallway. Faded tapestries depicting ancient battles hang on the walls. " +
            "Doors lead east and west. The castle entrance lies to the south.");
        Room armory = room("Armory",
            "Weapons of all kinds line the walls — most rusted beyond use, but a few still gleam. " +
            "The hallway is to the west.");
        Room library = room("Ancient Library",
            "Towering shelves hold countless tomes. Dust motes drift in a shaft of pale light. " +
            "A desk in the corner holds an open book. The hallway is to the east.");

        entrance = roomRepository.save(entrance);
        hallway  = roomRepository.save(hallway);
        armory   = roomRepository.save(armory);
        library  = roomRepository.save(library);

        entrance.setNorthId(hallway.getId());
        hallway.setSouthId(entrance.getId());
        hallway.setEastId(armory.getId());
        hallway.setWestId(library.getId());
        armory.setWestId(hallway.getId());
        library.setEastId(hallway.getId());

        roomRepository.save(entrance);
        roomRepository.save(hallway);
        roomRepository.save(armory);
        roomRepository.save(library);

        // ── Items ──────────────────────────────────────────────
        itemRepository.save(item("Torch",         "A wooden torch, still burning faintly.",      entrance));
        itemRepository.save(item("Short Sword",   "A battered but serviceable blade.",            armory));
        itemRepository.save(item("Wooden Shield", "Cracked but still sturdy.",                    armory));
        itemRepository.save(item("Ancient Scroll","Covered in faded runes you can't quite read.", library));
        itemRepository.save(item("Rusty Key",     "Heavy iron. What does it open?",               hallway));

        // ── Cantrips ───────────────────────────────────────────
        // Damage — Evocation
        cantripRepository.save(c("fire_bolt",    "Fire Bolt",       "Evocation",
            "Hurl a mote of fire. Deals 1d10 fire damage on a hit.",
            "mage", "fire", "damage"));
        cantripRepository.save(c("ray_of_frost", "Ray of Frost",    "Evocation",
            "A frigid beam of blue-white light. Deals 1d8 cold damage and reduces the target's speed.",
            "mage", "cold", "damage"));
        cantripRepository.save(c("shock_grasp",  "Shocking Grasp",  "Evocation",
            "Lightning springs from your hand. Deals 1d8 lightning damage; target can't take reactions.",
            "mage", "lightning", "damage"));
        cantripRepository.save(c("sacred_flame", "Sacred Flame",    "Evocation",
            "Flame-like radiance descends on a creature. Deals 1d8 radiant damage.",
            "cleric,paladin", "radiant", "damage"));
        cantripRepository.save(c("eldritch",     "Eldritch Blast",  "Evocation",
            "A beam of crackling force energy streaks toward a creature. Deals 1d10 force damage.",
            "mage,rogue", "force", "damage"));

        // Damage — Necromancy / Conjuration
        cantripRepository.save(c("toll_dead",    "Toll the Dead",   "Necromancy",
            "The sound of a doleful bell fills the air. Deals 1d8 necrotic damage (1d12 if already wounded).",
            "mage,cleric", "necrotic", "damage"));
        cantripRepository.save(c("chill_touch",  "Chill Touch",     "Necromancy",
            "A ghostly hand chills the creature. Deals 1d8 necrotic damage and prevents the target from regaining HP.",
            "mage", "necrotic", "debuff"));
        cantripRepository.save(c("acid_splash",  "Acid Splash",     "Conjuration",
            "Hurl a bubble of acid. Deals 1d6 acid damage.",
            "mage,rogue", "acid", "damage"));
        cantripRepository.save(c("poison_spray", "Poison Spray",    "Conjuration",
            "Project a cone of noxious gas. Deals 1d12 poison damage.",
            "mage,cleric,ranger", "poison", "damage"));

        // Damage — Transmutation / Enchantment
        cantripRepository.save(c("thorn_whip",   "Thorn Whip",      "Transmutation",
            "A vine-like whip of thorns strikes a creature. Deals 1d6 piercing damage and pulls the target 10 feet closer.",
            "cleric,ranger", "piercing", "damage"));
        cantripRepository.save(c("vicious_mock", "Vicious Mockery", "Enchantment",
            "Unleash a string of insults laced with subtle enchantments. Deals 1d4 psychic damage; target has disadvantage on its next attack.",
            "mage", "psychic", "debuff"));

        // Utility
        cantripRepository.save(c("mage_hand",    "Mage Hand",        "Conjuration",
            "A spectral floating hand appears. You can use it to manipulate objects up to 30 feet away.",
            "mage,rogue", null, "utility"));
        cantripRepository.save(c("minor_illus",  "Minor Illusion",   "Illusion",
            "Create a sound or an image of an object within range. Lasts 1 minute.",
            "mage,rogue", null, "utility"));
        cantripRepository.save(c("light",        "Light",            "Evocation",
            "Touch one object. It sheds bright light in a 20-foot radius and dim light in an additional 20 feet.",
            "mage,cleric,paladin", null, "utility"));
        cantripRepository.save(c("prestidig",    "Prestidigitation", "Transmutation",
            "Perform minor magical tricks: light candles, clean objects, create brief sensory effects.",
            "mage,rogue", null, "utility"));

        // Buff / Support
        cantripRepository.save(c("guidance",     "Guidance",         "Divination",
            "Touch a willing creature. Once before the spell ends it may add 1d4 to one ability check.",
            "cleric", null, "buff"));
        cantripRepository.save(c("resistance",   "Resistance",       "Abjuration",
            "Touch a willing creature. Once before the spell ends it may add 1d4 to one saving throw.",
            "cleric,paladin", null, "buff"));
        cantripRepository.save(c("blade_ward",   "Blade Ward",       "Abjuration",
            "Extend your hand and trace a sigil of warding. Until the end of your next turn you have resistance to bludgeoning, piercing, and slashing damage.",
            "warrior,paladin,rogue", null, "buff"));
        cantripRepository.save(c("true_strike",  "True Strike",      "Divination",
            "Gain brief insight into a target's defenses. You have advantage on your next attack roll against the creature.",
            "mage,rogue,ranger,warrior", null, "buff"));

        // Healing
        cantripRepository.save(c("spare_dying",  "Spare the Dying",  "Necromancy",
            "Touch a living creature at 0 hit points. It becomes stable and ceases making death saving throws.",
            "cleric", null, "healing"));
    }

    // ── Helpers ────────────────────────────────────────────
    private Room room(String name, String description) {
        Room r = new Room(); r.setName(name); r.setDescription(description); return r;
    }

    private Item item(String name, String description, Room room) {
        Item i = new Item(); i.setName(name); i.setDescription(description); i.setRoom(room); return i;
    }

    private Cantrip c(String id, String name, String school, String desc, String classes, String dmgType, String effect) {
        return new Cantrip(id, name, school, desc, classes, dmgType, effect);
    }
}
