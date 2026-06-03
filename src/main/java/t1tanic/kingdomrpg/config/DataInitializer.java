package t1tanic.kingdomrpg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.Room;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoomRepository     roomRepository;
    private final RoomInitializer    roomInitializer;
    private final ItemInitializer    itemInitializer;
    private final CantripInitializer cantripInitializer;

    @EventListener(ApplicationReadyEvent.class)
    public void seedWorld() {
        if (roomRepository.count() > 0) return;

        Map<String, Room> rooms = roomInitializer.seed();
        itemInitializer.seed(rooms);
        cantripInitializer.seed();
    }
}
