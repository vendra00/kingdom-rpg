package t1tanic.kingdomrpg.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.item.*;
import t1tanic.kingdomrpg.domain.enums.DamageType;
import t1tanic.kingdomrpg.domain.item.enums.WeaponRange;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemInitializer {

    private final ItemRepository itemRepository;

    public void seed(Map<String, Room> rooms) {
        log.debug("Seeding items...");
        var items = List.of(

            weapon("Short Sword",   "A battered but serviceable blade.",      rooms.get("armory"),   1_500, 2, 5,  WeaponRange.MELEE,  DamageType.SLASHING, 100),
            weapon("Dagger",        "Small, light, and easy to conceal.",      rooms.get("armory"),     500, 1, 3,  WeaponRange.MELEE,  DamageType.PIERCING,  80),

            armor ("Leather Armor", "Supple but protective.",                  rooms.get("armory"),   4_500, 11, "Light",  150),

            shield("Wooden Shield", "Cracked but still sturdy.",               rooms.get("armory"),   2_500, 2, 120),

            consumable("Torch",        "A wooden torch, still burning faintly.",  rooms.get("entrance"), 300, "Provides bright light for 1 hour.", 1),
            consumable("Health Potion", "A vial of red liquid. Restores HP.",      rooms.get("hallway"),  200, "Restores 2d4+2 hit points when consumed.", 1),

            keyItem("Ancient Scroll", "Covered in faded runes you can't quite read.", rooms.get("library"), 200),
            keyItem("Rusty Key",      "Heavy iron. What does it open?",               rooms.get("hallway"), 150),

            // Hidden items — require search or perception to discover
            hide(consumable("Iron Rations", "Stale but still edible. Better than nothing.",
                            rooms.get("armory"), 400, "Restores 1d4 stamina.", 2),
                 "a dusty storage crate", 10),
            hide(keyItem("Cipher Key", "A small brass key etched with strange symbols.",
                         rooms.get("library"), 50),
                 "a hollow book", 16),
            hide(weapon("Bone Knife", "Crude but sharp. Made from something best left unidentified.",
                        rooms.get("hallway"), 300, 1, 2, WeaponRange.MELEE, DamageType.PIERCING, 60),
                 null, 19)   // no container — must pass raw perception
        );
        itemRepository.saveAll(items);
        log.info("Seeded {} items", items.size());
    }

    private Weapon weapon(String name, String desc, Room room, int grams,
                          int attackMin, int attackMax, WeaponRange range,
                          DamageType dmgType, int maxDurability) {
        Weapon w = new Weapon();
        base(w, name, desc, room, grams, maxDurability);
        w.setAttackMin(attackMin);
        w.setAttackMax(attackMax);
        w.setWeaponRange(range);
        w.setDamageType(dmgType);
        return w;
    }

    private Armor armor(String name, String desc, Room room, int grams, int ac, String type, int maxDurability) {
        Armor a = new Armor();
        base(a, name, desc, room, grams, maxDurability);
        a.setArmorClass(ac);
        a.setArmorType(type);
        return a;
    }

    private Shield shield(String name, String desc, Room room, int grams, int bonus, int maxDurability) {
        Shield s = new Shield();
        base(s, name, desc, room, grams, maxDurability);
        s.setDefenseBonus(bonus);
        return s;
    }

    private Consumable consumable(String name, String desc, Room room, int grams, String effect, int charges) {
        Consumable c = new Consumable();
        base(c, name, desc, room, grams, 100);
        c.setEffect(effect);
        c.setCharges(charges);
        return c;
    }

    private <T extends t1tanic.kingdomrpg.domain.item.Item> T hide(T item, String container, int dc) {
        item.setVisible(false);
        item.setHiddenIn(container);
        item.setPerceptionDc(dc);
        return item;
    }

    private KeyItem keyItem(String name, String desc, Room room, int grams) {
        KeyItem k = new KeyItem();
        base(k, name, desc, room, grams, 100);
        return k;
    }

    private void base(Item item, String name, String desc, Room room, int grams, int maxDurability) {
        item.setName(name);
        item.setDescription(desc);
        item.setRoom(room);
        item.setWeightGrams(grams);
        item.setMaxDurability(maxDurability);
        item.setDurability(maxDurability);
    }
}
