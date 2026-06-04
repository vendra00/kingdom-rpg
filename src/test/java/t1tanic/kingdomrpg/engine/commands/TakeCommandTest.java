package t1tanic.kingdomrpg.engine.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.item.Weapon;
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TakeCommandTest {

    @Mock ItemRepository   itemRepository;
    @Mock PlayerRepository playerRepository;
    @InjectMocks TakeCommand takeCommand;

    private Player player;

    @BeforeEach
    void setUp() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(1L);
        player = new Player();
        player.setCurrentRoom(room);
    }

    @Test
    void noArgs_returnsPrompt() {
        assertThat(takeCommand.execute(player, new String[]{}))
            .isEqualTo("Take what?");
    }

    @Test
    void itemNotInRoom_returnsError() {
        when(itemRepository.findByRoomId(1L)).thenReturn(List.of());

        assertThat(takeCommand.execute(player, new String[]{"sword"}))
            .contains("There's no sword here");
    }

    @Test
    void itemTooHeavy_returnsError() {
        player.getResources().setCarryWeight(player.getMaxCarryWeight());

        Weapon sword = new Weapon();
        sword.setName("Greatsword");
        sword.setWeightGrams(3_000);
        when(itemRepository.findByRoomId(1L)).thenReturn(List.of(sword));

        assertThat(takeCommand.execute(player, new String[]{"greatsword"}))
            .contains("Too heavy");
    }

    @Test
    void success_picksUpItemAndUpdatesCarryWeight() {
        Weapon sword = new Weapon();
        sword.setName("Short Sword");
        sword.setWeightGrams(1_500);
        when(itemRepository.findByRoomId(1L)).thenReturn(List.of(sword));

        String result = takeCommand.execute(player, new String[]{"sword"});

        assertThat(result).contains("Short Sword").contains("1.50 kg");
        assertThat(player.getResources().getCarryWeight()).isEqualTo(1_500);
        verify(playerRepository).save(player);
    }
}
