package t1tanic.kingdomrpg.engine.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.NpcTrust;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.CharacterBackground;
import t1tanic.kingdomrpg.domain.character.enums.CharacterRace;
import t1tanic.kingdomrpg.repository.NpcTrustRepository;

/**
 * Manages the trust relationship between players and NPCs.
 * <p>On first interaction a starting value is calculated from the NPC's {@code baseTrust}
 * combined with small bonuses derived from the player's race and background.
 * Subsequent persuasion attempts and conversation outcomes adjust the level up or down,
 * always clamped to [0, 100].</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class NpcTrustService {

    private final NpcTrustRepository trustRepository;

    /**
     * Returns the existing trust record for the player–NPC pair, creating and persisting
     * a new one with a calculated starting value if none exists yet.
     *
     * @param player the player querying the NPC
     * @param npc    the NPC being interacted with
     * @return the current (or newly created) {@link NpcTrust} record
     */
    public NpcTrust getOrCreate(Player player, Npc npc) {
        return trustRepository.findByPlayerIdAndNpcId(player.getId(), npc.getId())
                .orElseGet(() -> {
                    NpcTrust trust = new NpcTrust();
                    trust.setPlayer(player);
                    trust.setNpc(npc);
                    trust.setTrustLevel(calculateStartingTrust(npc, player));
                    return trustRepository.save(trust);
                });
    }

    /**
     * Adjusts the trust level by {@code delta}, clamping the result to [0, 100], and persists.
     *
     * @param trust the trust record to modify
     * @param delta positive to increase trust, negative to decrease
     * @return the updated trust record
     */
    public NpcTrust adjustTrust(NpcTrust trust, int delta) {
        trust.setTrustLevel(Math.max(0, Math.min(100, trust.getTrustLevel() + delta)));
        return trustRepository.save(trust);
    }

    /**
     * Computes the starting trust level for a new player–NPC relationship.
     * <p>Uses the NPC's {@code baseTrust} as a foundation, then adds small bonuses based on
     * the player's race and background to reflect how different walks of life are perceived
     * in the castle's social context.</p>
     *
     * @param npc    the NPC whose base trust and faction define the floor
     * @param player the player whose identity provides the modifiers
     * @return clamped starting trust in [0, 100]
     */
    private int calculateStartingTrust(Npc npc, Player player) {
        int trust = npc.getBaseTrust();

        if (player.getIdentity() == null) return Math.max(0, Math.min(100, trust));

        CharacterRace race = player.getIdentity().getRace();
        if (race != null) {
            trust += switch (race) {
                case HUMAN    ->  5;  // common, unsuspicious
                case DWARF    ->  3;  // practical, respected
                case HALFLING ->  2;
                case ELF      -> -3;  // perceived as aloof
                case HALFORC  -> -8;  // instils wariness
                default       ->  0;
            };
        }

        CharacterBackground background = player.getIdentity().getBackground();
        if (background != null) {
            trust += switch (background) {
                case NOBLE    -> 10;  // status commands respect
                case SOLDIER  ->  8;  // martial discipline is understood
                case SCHOLAR  ->  6;  // scholars appreciate fellow learners
                case ACOLYTE  ->  4;
                case CRIMINAL -> -10; // untrustworthy reputation
                default       ->  0;
            };
        }

        return Math.max(0, Math.min(100, trust));
    }
}
