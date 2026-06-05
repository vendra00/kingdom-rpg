package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import t1tanic.kingdomrpg.domain.BaseEntity;

/**
 * Tracks the trust relationship between a specific player and a specific NPC.
 * <p>The trust level (0–100) starts at a value derived from the NPC's base trust,
 * the player's race, and their background. It changes over time through persuasion attempts
 * and is used by the LLM system prompt to gate what information the NPC is willing to share.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "npc_id"}))
@Getter
@Setter
@NoArgsConstructor
public class NpcTrust extends BaseEntity {

    /**
     * The player whose relationship with the NPC this record tracks.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    /**
     * The NPC whose relationship with the player this record tracks.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id", nullable = false)
    private Npc npc;

    /**
     * Current trust value, clamped to [0, 100].
     */
    private int trustLevel;
}
