package t1tanic.kingdomrpg.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import t1tanic.kingdomrpg.TestcontainersConfiguration;
import t1tanic.kingdomrpg.domain.Player;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class PlayerRepositoryTest {

    @Autowired PlayerRepository playerRepository;

    @Test
    void findByName_existingPlayer_returnsPlayer() {
        Player p = new Player();
        p.setName("Aragorn");
        playerRepository.saveAndFlush(p);

        Optional<Player> result = playerRepository.findByName("Aragorn");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Aragorn");
    }

    @Test
    void findByName_nonexistentPlayer_returnsEmpty() {
        assertThat(playerRepository.findByName("Nobody")).isEmpty();
    }

    @Test
    void save_assignsSurrogateKeyAndVersion() {
        Player p = new Player();
        p.setName("Legolas");
        Player saved = playerRepository.saveAndFlush(p);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getVersion()).isNotNull();
    }
}
