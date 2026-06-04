package t1tanic.kingdomrpg.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomInitializer {

    private final RoomRepository roomRepository;

    public Map<String, Room> seed() {
        log.debug("Seeding rooms...");
        Room entrance = roomRepository.save(room("Castle Entrance",
            "You stand at the imposing gates of an ancient castle. Stone walls rise around you, " +
            "covered in moss and ivy. A torchlit corridor stretches to the north."));
        Room hallway = roomRepository.save(room("Great Hallway",
            "A long, dimly lit hallway. Faded tapestries depicting ancient battles hang on the walls. " +
            "Doors lead east and west. The castle entrance lies to the south."));
        Room armory = roomRepository.save(room("Armory",
            "Weapons of all kinds line the walls — most rusted beyond use, but a few still gleam. " +
            "The hallway is to the west."));
        Room library = roomRepository.save(room("Ancient Library",
            "Towering shelves hold countless tomes. Dust motes drift in a shaft of pale light. " +
            "A desk in the corner holds an open book. The hallway is to the east."));

        entrance.setNorthId(hallway.getId());
        hallway.setSouthId(entrance.getId());
        hallway.setEastId(armory.getId());
        hallway.setWestId(library.getId());
        armory.setWestId(hallway.getId());
        library.setEastId(hallway.getId());

        roomRepository.saveAll(List.of(entrance, hallway, armory, library));

        var rooms = Map.of(
            "entrance", entrance,
            "hallway",  hallway,
            "armory",   armory,
            "library",  library
        );
        log.info("Seeded {} rooms", rooms.size());
        return rooms;
    }

    private Room room(String name, String description) {
        Room r = new Room();
        r.setName(name);
        r.setDescription(description);
        return r;
    }
}
