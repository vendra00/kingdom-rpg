package t1tanic.kingdomrpg.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository roomRepository;
    private final RoomInitializer roomInitializer;
    private final ItemInitializer itemInitializer;
    private final CantripInitializer cantripInitializer;
    private final NpcInitializer npcInitializer;

    @EventListener(ApplicationReadyEvent.class)
    public void seedWorld() {
        if (roomRepository.count() > 0) {
            log.info("World already seeded — skipping");
            return;
        }
        log.info("Seeding world...");
        Map<String, Room> rooms = roomInitializer.seed();
        itemInitializer.seed(rooms);
        cantripInitializer.seed();
        npcInitializer.seed(rooms);
        log.info("World seeded — {} rooms ready", rooms.size());
    }
}
