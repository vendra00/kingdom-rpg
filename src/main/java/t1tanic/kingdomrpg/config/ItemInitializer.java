package t1tanic.kingdomrpg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.repository.ItemRepository;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItemInitializer {

    private final ItemRepository itemRepository;

    public void seed(Map<String, Room> rooms) {
        itemRepository.saveAll(List.of(
            item("Torch",          "A wooden torch, still burning faintly.",       rooms.get("entrance"),   300),
            item("Short Sword",    "A battered but serviceable blade.",             rooms.get("armory"),   1_500),
            item("Wooden Shield",  "Cracked but still sturdy.",                     rooms.get("armory"),   2_500),
            item("Ancient Scroll", "Covered in faded runes you can't quite read.",  rooms.get("library"),    200),
            item("Rusty Key",      "Heavy iron. What does it open?",                rooms.get("hallway"),    150)
        ));
    }

    private Item item(String name, String description, Room room, int weightGrams) {
        Item i = new Item();
        i.setName(name);
        i.setDescription(description);
        i.setRoom(room);
        i.setWeightGrams(weightGrams);
        return i;
    }
}
