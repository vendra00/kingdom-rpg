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
import t1tanic.kingdomrpg.domain.world.Room;
import t1tanic.kingdomrpg.repository.PlayerRepository;
import t1tanic.kingdomrpg.repository.RoomRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoCommandTest {

    @Mock RoomRepository   roomRepository;
    @Mock PlayerRepository playerRepository;
    @Mock LookCommand      lookCommand;
    @InjectMocks GoCommand goCommand;

    private Player player;
    private Room   currentRoom;

    @BeforeEach
    void setUp() {
        currentRoom = mock(Room.class);
        when(currentRoom.getName()).thenReturn("Castle Entrance");
        player = new Player();
        player.setCurrentRoom(currentRoom);
        when(lookCommand.execute(any(), any())).thenReturn("You see a room.");
    }

    @Test
    void noArgs_returnsDirectionPrompt() {
        assertThat(goCommand.execute(player, new String[]{}))
            .contains("Go where?");
    }

    @Test
    void blockedDirection_returnsError() {
        when(currentRoom.getNorthId()).thenReturn(null);

        assertThat(goCommand.execute(player, new String[]{"north"}))
            .contains("can't go north");
    }

    @Test
    void unknownDirection_returnsError() {
        assertThat(goCommand.execute(player, new String[]{"upstairs"}))
            .contains("Unknown direction");
    }

    @Test
    void validDirection_movesPlayerAndSaves() {
        Room nextRoom = mock(Room.class);
        when(currentRoom.getNorthId()).thenReturn(2L);
        when(roomRepository.findById(2L)).thenReturn(Optional.of(nextRoom));

        goCommand.execute(player, new String[]{"north"});

        assertThat(player.getCurrentRoom()).isEqualTo(nextRoom);
        verify(playerRepository).save(player);
    }
}
