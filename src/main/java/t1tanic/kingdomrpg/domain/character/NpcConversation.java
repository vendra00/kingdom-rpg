package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import t1tanic.kingdomrpg.domain.BaseEntity;
import t1tanic.kingdomrpg.domain.character.enums.NpcConversationRole;

/**
 * Persists a single message turn in a dialogue between a specific player and NPC.
 * <p>Stored in insertion order via the inherited {@code createdAt} audit field so that the
 * full conversation history can be replayed to the LLM on each new turn.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class NpcConversation extends BaseEntity {

    /**
     * The player who participated in this conversation turn.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /**
     * The NPC that the player is speaking with.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id", nullable = false)
    private Npc npc;

    /**
     * Whether this message was produced by the player ({@code USER}) or the NPC ({@code ASSISTANT}).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NpcConversationRole role;

    /**
     * The raw text content of this message turn.
     */
    @Column(length = 2000, nullable = false)
    private String content;
}
