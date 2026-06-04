package t1tanic.kingdomrpg.engine;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import t1tanic.kingdomrpg.TestcontainersConfiguration;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class GameEngineIntegrationTest {

    @Autowired GameEngine        gameEngine;
    @Autowired PlayerRepository  playerRepository;

    // ── Helpers ──────────────────────────────────────────

    private Map<String, Object> payload(String race, String cls) {
        return Map.of(
            "race",            race,
            "characterClass",  cls,
            "gender",          "other",
            "background",      "scholar",
            "attributes",      Map.of("strength", 8, "dexterity", 14, "constitution", 10,
                                      "intelligence", 15, "wisdom", 12, "charisma", 8),
            "cantrips",        List.of("fire_bolt")
        );
    }

    // ── Join ─────────────────────────────────────────────

    @Test
    void joinGame_newPlayer_createsCharacterAndPersists() {
        String result = gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        assertThat(result).contains("Character created");
        assertThat(playerRepository.findByName("Gandalf")).isPresent();
    }

    @Test
    void joinGame_returningPlayer_welcomesBack() {
        gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        String result = gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        assertThat(result).contains("Welcome back");
    }

    @Test
    void joinGame_newPlayer_attributeBonusesApplied() {
        gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        var player = playerRepository.findByName("Gandalf").orElseThrow();
        // Elf: +2 DEX, +1 INT; Scholar: +1 INT — DEX 14+2=16, INT 15+1+1=17
        assertThat(player.getAttributes().getDexterity()).isEqualTo(16);
        assertThat(player.getAttributes().getIntelligence()).isEqualTo(17);
    }

    // ── Commands ─────────────────────────────────────────

    @Test
    void processCommand_look_returnsStartingRoomName() {
        gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        String result = gameEngine.processCommand("Gandalf", "look");

        assertThat(result).containsIgnoringCase("castle entrance");
    }

    @Test
    void processCommand_unknownVerb_returnsHelpSuggestion() {
        gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        String result = gameEngine.processCommand("Gandalf", "dance");

        assertThat(result).contains("don't understand");
    }

    @Test
    void processCommand_status_containsPlayerName() {
        gameEngine.joinGame("Gandalf", payload("elf", "mage"));

        String result = gameEngine.processCommand("Gandalf", "status");

        assertThat(result).containsIgnoringCase("gandalf");
    }
}
