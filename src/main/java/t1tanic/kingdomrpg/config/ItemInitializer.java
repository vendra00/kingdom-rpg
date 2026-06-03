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
            item("Torch",          "A wooden torch, still burning faintly.",       rooms.get("entrance")),
            item("Short Sword",    "A battered but serviceable blade.",             rooms.get("armory")),
            item("Wooden Shield",  "Cracked but still sturdy.",                     rooms.get("armory")),
            item("Ancient Scroll", "Covered in faded runes you can't quite read.",  rooms.get("library")),
            item("Rusty Key",      "Heavy iron. What does it open?",                rooms.get("hallway"))
        ));
    }

    private Item item(String name, String description, Room room) {
        Item i = new Item();
        i.setName(name);
        i.setDescription(description);
        i.setRoom(room);
        return i;
    }
}
