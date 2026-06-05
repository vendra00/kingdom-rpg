package t1tanic.kingdomrpg.config.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.magic.enums.CantripDef;
import t1tanic.kingdomrpg.repository.CantripRepository;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class CantripInitializer {

    private final CantripRepository cantripRepository;

    public void seed() {
        log.debug("Seeding cantrips...");
        var cantrips = Arrays.stream(CantripDef.values()).map(CantripDef::toEntity).toList();
        cantripRepository.saveAll(cantrips);
        log.info("Seeded {} cantrips", cantrips.size());
    }
}
