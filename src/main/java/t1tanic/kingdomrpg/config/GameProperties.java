package t1tanic.kingdomrpg.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Centralised game-balance configuration bound from the {@code game.*} property namespace.
 * <p>All tunable numbers that would otherwise be scattered as magic constants across the engine
 * are declared here with documented defaults, making balance adjustments a properties-file change
 * rather than a recompile.</p>
 *
 * <pre>
 * game.npc.conversation-history-limit  — turns kept in LLM context   (default 20)
 * game.npc.trust-delta-min             — floor on LLM trust signal    (default -15)
 * game.npc.trust-delta-max             — ceiling on LLM trust signal  (default  +8)
 * game.ability-check.trust-crit-success — bonus on a critical roll    (default  +6)
 * game.ability-check.trust-success      — bonus on a plain success    (default  +3)
 * game.ability-check.trust-failure      — penalty on a plain failure  (default  -2)
 * game.ability-check.trust-fumble       — penalty on a fumble         (default  -6)
 * </pre>
 *
 * @author t1tanic
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "game")
public class GameProperties {

    private Npc          npc          = new Npc();
    private AbilityCheck abilityCheck = new AbilityCheck();

    @Data
    public static class Npc {
        /** Maximum conversation turns passed to the LLM as context. */
        private int conversationHistoryLimit = 20;
        /** Minimum trust delta accepted from the LLM's [TRUST:N] signal. */
        private int trustDeltaMin = -15;
        /** Maximum trust delta accepted from the LLM's [TRUST:N] signal. */
        private int trustDeltaMax = 8;
        /** Gold deducted from the player when they attempt a bribe (win or lose). */
        private int bribeCost = 5;
    }

    @Data
    public static class AbilityCheck {
        /** Trust awarded on a critical success (natural 20). */
        private int trustCritSuccess = 6;
        /** Trust awarded on a plain success. */
        private int trustSuccess = 3;
        /** Trust penalty on a plain failure. */
        private int trustFailure = -2;
        /** Trust penalty on a fumble (natural 1). */
        private int trustFumble = -6;
    }
}
