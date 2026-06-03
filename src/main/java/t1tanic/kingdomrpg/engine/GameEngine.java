package t1tanic.kingdomrpg.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import t1tanic.kingdomrpg.domain.*;
import t1tanic.kingdomrpg.repository.CantripRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static t1tanic.kingdomrpg.domain.Attribute.*;

@Service
@RequiredArgsConstructor
public class GameEngine {

    private final CommandParser commandParser;
    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;
    private final CantripRepository cantripRepository;

    @Transactional
    public String joinGame(String name, Map<String, Object> payload) {
        boolean isNew = playerRepository.findByName(name).isEmpty();
        if (isNew) {
            createNewPlayer(name, payload);
            return "Character created!  Type 'help' for commands.\n";
        }
        return "Welcome back, " + name + "!  Your adventure continues.\n";
    }

    @Transactional
    public String processCommand(String playerName, String input) {
        Player player = playerRepository.findByName(playerName)
            .orElseThrow(() -> new IllegalStateException("Player not found — please rejoin."));
        return commandParser.parse(player, input);
    }

    @SuppressWarnings("unchecked")
    private Player createNewPlayer(String name, Map<String, Object> payload) {
        Room startRoom = roomRepository.findById(1L)
            .orElseThrow(() -> new IllegalStateException("Start room not found"));

        String raceStr       = (String) payload.getOrDefault("race",            "human");
        String classStr      = (String) payload.getOrDefault("characterClass",  "warrior");
        String genderStr     = (String) payload.getOrDefault("gender",          "other");
        String backgroundStr = (String) payload.getOrDefault("background",      "noble");

        CharacterRace       race = CharacterRace.fromString(raceStr);
        CharacterBackground bg   = CharacterBackground.fromString(backgroundStr);

        // Start from point-buy values sent by the client
        Map<Attribute, Integer> base = new EnumMap<>(Attribute.class);
        if (payload.get("attributes") instanceof Map<?, ?> raw) {
            raw.forEach((k, v) -> base.put(Attribute.fromKey((String) k), ((Number) v).intValue()));
        }
        for (Attribute attr : Attribute.values()) {
            base.putIfAbsent(attr, 10);
        }

        // Apply racial + background bonuses
        race.getBonuses().forEach((k, v) -> base.merge(k, v, Integer::sum));
        bg.getBonuses()  .forEach((k, v) -> base.merge(k, v, Integer::sum));

        Player player = new Player();
        player.setName(name);
        player.setCurrentRoom(startRoom);
        player.setIdentity(new CharacterIdentity(raceStr, classStr, genderStr, backgroundStr));
        player.setAttributes(new CharacterAttributes(
            base.get(STRENGTH),
            base.get(DEXTERITY),
            base.get(CONSTITUTION),
            base.get(INTELLIGENCE),
            base.get(WISDOM),
            base.get(CHARISMA)
        ));

        CharacterResources res = player.getResources();
        res.setHealth( player.getMaxHealth());
        res.setMana(   player.getMaxMana());
        res.setStamina(player.getMaxStamina());

        player.setLearnedCantrips(resolveCantrips(payload.get("cantrips")));
        return playerRepository.save(player);
    }

    @SuppressWarnings("unchecked")
    private Set<Cantrip> resolveCantrips(Object raw) {
        Set<Cantrip> result = new HashSet<>();
        if (raw instanceof List<?> ids) {
            ids.forEach(id -> cantripRepository.findById((String) id).ifPresent(result::add));
        }
        return result;
    }
}
