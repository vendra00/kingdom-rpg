package t1tanic.kingdomrpg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Item;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository roomRepository;
    private final ItemRepository itemRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedWorld() {
        if (roomRepository.count() > 0) return;

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

        itemRepository.save(item("Torch",        "A wooden torch, still burning faintly.", entrance));
        itemRepository.save(item("Short Sword",  "A battered but serviceable blade.",       armory));
        itemRepository.save(item("Wooden Shield","Cracked but still sturdy.",               armory));
        itemRepository.save(item("Ancient Scroll","Covered in faded runes you can't quite read.", library));
        itemRepository.save(item("Rusty Key",    "Heavy iron. What does it open?",          hallway));
    }

    private Room room(String name, String description) {
        Room r = new Room();
        r.setName(name);
        r.setDescription(description);
        return r;
    }

    private Item item(String name, String description, Room room) {
        Item i = new Item();
        i.setName(name);
        i.setDescription(description);
        i.setRoom(room);
        return i;
    }
}
