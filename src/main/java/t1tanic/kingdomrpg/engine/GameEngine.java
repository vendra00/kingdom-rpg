package t1tanic.kingdomrpg.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import t1tanic.kingdomrpg.domain.character.*;
import t1tanic.kingdomrpg.domain.character.enums.Attribute;
import t1tanic.kingdomrpg.domain.character.enums.CharacterBackground;
import t1tanic.kingdomrpg.domain.character.enums.CharacterClass;
import t1tanic.kingdomrpg.domain.character.enums.CharacterRace;
import t1tanic.kingdomrpg.domain.magic.Cantrip;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.CantripRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Core transactional service orchestration engine driving character initialization and command dispatch loops.
 * <p>The {@code GameEngine} acts as the primary API bridge, routing user connections, parsing upstream configuration
 * payloads to construct persistent character hierarchies, and dispatching inbound commands down to the domain layer.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameEngine {

    private final CommandParser commandParser;
    private final PlayerRepository playerRepository;
    private final RoomRepository roomRepository;
    private final CantripRepository cantripRepository;

    /**
     * Resolves an entry sequence request for a specific entity nickname.
     * <p>If the name matching evaluation checks out clean against the underlying storage system,
     * a fresh {@link Player} graph is automatically initialized and committed using payload properties.
     * Otherwise, reconnecting metadata states are retrieved.</p>
     *
     * @param name    the unique player or account user identity handle
     * @param payload the configuration matrix containing point-buy attributes, race selection, and spell choices
     * @return a narrative context string indicating either initialization confirmation or a welcome fallback message
     */
    @Transactional
    public String joinGame(String name, Map<String, Object> payload) {
        boolean isNew = playerRepository.findByName(name).isEmpty();
        if (isNew) {
            createNewPlayer(name, payload);
            return "Character created!  Type 'help' for commands.\n";
        }
        log.info("Returning player reconnected");
        return "Welcome back, " + name + "!  Your adventure continues.\n";
    }

    /**
     * Delegates text input parsing downstream within an isolated transaction layer.
     *
     * @param playerName the identifier token matching an active player entity record
     * @param input      the raw command string entered by the user
     * @return the execution response string processed by the parsing infrastructure
     * @throws IllegalStateException if no player matching the provided name parameter exists in storage
     */
    @Transactional
    public String processCommand(String playerName, String input) {
        Player player = playerRepository.findByName(playerName)
            .orElseThrow(() -> new IllegalStateException("Player not found — please rejoin."));
        String result = commandParser.parse(player, input);
        return result + "\n" + statsTag(player);
    }

    private String statsTag(Player player) {
        var res = player.getResources();
        return "[stats]%d,%d,%d,%d,%d,%d,%d,%d[/stats]".formatted(
            res.getHealth(),      player.getMaxHealth(),
            res.getMana(),        player.getMaxMana(),
            res.getStamina(),     player.getMaxStamina(),
            res.getCarryWeight(), player.getMaxCarryWeight()
        );
    }

    /**
     * Performs a multi-stage structural initialization of a new player instance.
     * <p>The building pipeline steps include:</p>
     * <ol>
     * <li>Binding the session reference to the standard starting point environment container index.</li>
     * <li>Parsing metadata characteristics (Race, Class, Biography parameters).</li>
     * <li>Mapping point-buy stats, safety checking baseline configurations, and adding race/background bonuses.</li>
     * <li>Evaluating maximum capacity values to calculate starting health, stamina, and mana pools.</li>
     * <li>Querying and associating eligible starting cantrip collections.</li>
     * </ol>
     *
     * @param name    the user identifier tag name
     * @param payload the raw parameter map configuration payload
     * @return a managed, fully updated {@link Player} domain state reference
     * @throws IllegalStateException if the fallback starter room configuration index cannot be resolved
     */
    @SuppressWarnings("unchecked")
    private Player createNewPlayer(String name, Map<String, Object> payload) {
        Room startRoom = roomRepository.findById(1L)
            .orElseThrow(() -> new IllegalStateException("Start room not found"));

        String raceStr       = (String) payload.getOrDefault("race",            "human");
        String classStr      = (String) payload.getOrDefault("characterClass",  "warrior");
        String genderStr     = (String) payload.getOrDefault("gender",          "other");
        String backgroundStr = (String) payload.getOrDefault("background",      "noble");

        CharacterRace       race      = CharacterRace.fromString(raceStr);
        CharacterClass      charClass = CharacterClass.fromString(classStr);
        CharacterBackground bg        = CharacterBackground.fromString(backgroundStr);
        log.info("Creating new player — {}/{} background:{}", race, charClass, bg);

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
        player.setIdentity(new CharacterIdentity(race, charClass, genderStr, bg));
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
        res.setGold(10);

        player.setLearnedCantrips(resolveCantrips(payload.get("cantrips")));
        return playerRepository.save(player);
    }

    /**
     * Resolves a generic list parameter containing text identifier tokens back into persistent entities.
     * Unrecognized key lookups are dropped safely from aggregation steps.
     *
     * @param raw the list array object reference extracted from the payload
     * @return a distinct, updated {@link Set} collection containing the confirmed managed {@link Cantrip} entities
     */
    @SuppressWarnings("unchecked")
    private Set<Cantrip> resolveCantrips(Object raw) {
        Set<Cantrip> result = new HashSet<>();
        if (raw instanceof List<?> ids) {
            ids.forEach(id -> cantripRepository.findById((String) id).ifPresent(result::add));
        }
        return result;
    }
}