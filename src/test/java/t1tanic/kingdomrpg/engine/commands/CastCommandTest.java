package t1tanic.kingdomrpg.engine.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import t1tanic.kingdomrpg.domain.Cantrip;
import t1tanic.kingdomrpg.domain.CantripEffect;
import t1tanic.kingdomrpg.domain.Player;

import static org.assertj.core.api.Assertions.assertThat;

class CastCommandTest {

    private final CastCommand castCommand = new CastCommand();
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    void noArgs_returnsPrompt() {
        assertThat(castCommand.execute(player, new String[]{}))
            .contains("Cast what?");
    }

    @Test
    void unknownCantrip_returnsError() {
        assertThat(castCommand.execute(player, new String[]{"fireball"}))
            .contains("don't know that cantrip");
    }

    @Test
    void damageCantrip_rollsDamageAndIncludesNoEnemyMessage() {
        player.getLearnedCantrips().add(new Cantrip(
            "fire_bolt", "Fire Bolt", "Evocation",
            "Hurl a mote of fire.", "mage", "fire", "1d10", CantripEffect.DAMAGE));

        String result = castCommand.execute(player, new String[]{"fire", "bolt"});

        assertThat(result).contains("Fire Bolt");
        assertThat(result).contains("fire damage");
        assertThat(result).contains("No enemy present");
    }

    @Test
    void utilityCantrip_doesNotIncludeCombatMessage() {
        player.getLearnedCantrips().add(new Cantrip(
            "mage_hand", "Mage Hand", "Conjuration",
            "A spectral floating hand.", "mage", null, null, CantripEffect.UTILITY));

        String result = castCommand.execute(player, new String[]{"mage", "hand"});

        assertThat(result).contains("Mage Hand");
        assertThat(result).doesNotContain("No enemy present");
    }
}
