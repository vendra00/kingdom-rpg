package t1tanic.kingdomrpg.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.item.*;
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

            weapon("Short Sword",   "A battered but serviceable blade.",      rooms.get("armory"),   1_500, "1d6",  "slashing"),
            weapon("Dagger",        "Small, light, and easy to conceal.",      rooms.get("armory"),     500, "1d4",  "piercing"),

            armor ("Leather Armor", "Supple but protective.",                  rooms.get("armory"),   4_500, 11, "Light"),

            shield("Wooden Shield", "Cracked but still sturdy.",               rooms.get("armory"),   2_500, 2),

            consumable("Torch",     "A wooden torch, still burning faintly.",  rooms.get("entrance"),   300, "Provides bright light for 1 hour.", 1),
            consumable("Health Potion", "A vial of red liquid. Restores HP.",  rooms.get("hallway"),    200, "Restores 2d4+2 hit points when consumed.", 1),

            keyItem("Ancient Scroll", "Covered in faded runes you can't quite read.", rooms.get("library"), 200),
            keyItem("Rusty Key",      "Heavy iron. What does it open?",               rooms.get("hallway"), 150)
        );
        itemRepository.saveAll(items);
        log.info("Seeded {} items", items.size());
    }

    private Weapon weapon(String name, String desc, Room room, int grams, String dice, String dmgType) {
        Weapon w = new Weapon();
        base(w, name, desc, room, grams);
        w.setDamageDice(dice);
        w.setDamageType(dmgType);
        return w;
    }

    private Armor armor(String name, String desc, Room room, int grams, int ac, String type) {
        Armor a = new Armor();
        base(a, name, desc, room, grams);
        a.setArmorClass(ac);
        a.setArmorType(type);
        return a;
    }

    private Shield shield(String name, String desc, Room room, int grams, int bonus) {
        Shield s = new Shield();
        base(s, name, desc, room, grams);
        s.setDefenseBonus(bonus);
        return s;
    }

    private Consumable consumable(String name, String desc, Room room, int grams, String effect, int charges) {
        Consumable c = new Consumable();
        base(c, name, desc, room, grams);
        c.setEffect(effect);
        c.setCharges(charges);
        return c;
    }

    private KeyItem keyItem(String name, String desc, Room room, int grams) {
        KeyItem k = new KeyItem();
        base(k, name, desc, room, grams);
        return k;
    }

    private void base(Item item, String name, String desc, Room room, int grams) {
        item.setName(name);
        item.setDescription(desc);
        item.setRoom(room);
        item.setWeightGrams(grams);
    }
}
