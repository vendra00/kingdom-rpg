package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.domain.character.enums.NpcFaction;

import java.util.Optional;

/**
 * Represents a non-player character (NPC) within the RPG world.
 * <p>Extends {@link BaseCharacter} for shared fields and adds NPC-specific properties:
 * faction disposition, a room-presence description, a greeting delivered on {@code talk},
 * and a challenge level.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Npc extends BaseCharacter {

    /**
     * The NPC's relationship toward the player.
     * Determines whether they respond to dialogue and how they are colored in room output.
     */
    @Enumerated(EnumType.STRING)
    private NpcFaction faction = NpcFaction.NEUTRAL;

    /**
     * A brief physical or behavioral description shown when the NPC is visible in a room.
     */
    @Column(length = 500)
    private String description;

    /**
     * The line this NPC delivers when the player uses {@code talk <name>}.
     * {@code null} for hostile NPCs or those that simply don't respond.
     */
    @Column(length = 500)
    private String greeting;

    /**
     * Challenge level used to scale combat stats and XP rewards in future combat.
     */
    private int level = 1;

    /**
     * Extended backstory and personality description fed to the LLM as part of the system prompt.
     * Falls back to {@link #description} if blank.
     */
    @Column(length = 1000)
    private String personality;

    /**
     * Starting trust value before player-trait modifiers are applied.
     * Typically 50 for friendly NPCs, 25 for neutral, 0 for hostile.
     */
    private int baseTrust = 25;

    /**
     * Hidden information this NPC will only share once trust reaches {@link #secretThreshold}.
     * The LLM is instructed to reveal it naturally in conversation when the threshold is met.
     */
    @Column(length = 1000)
    private String secret;

    /**
     * Minimum trust level required to unlock the {@link #secret}.
     */
    private int secretThreshold = 70;

    /**
     * Difficulty class for persuasion attempts ({@code d20 + CHA modifier} vs this value).
     */
    private int persuadeDc = 15;

    /**
     * Whether this NPC is currently visible and interactable by the player.
     * Hidden NPCs (e.g., concealed behind a wall, inside a container, or cloaked) are not
     * shown in room descriptions and cannot be talked to or persuaded until revealed.
     */
    private boolean visible = true;

    /**
     * Per-ability persuasion outcome flavor text.
     * Each field is optional — only outcomes explicitly authored appear in gameplay.
     */
    @Embedded
    private NpcAbilityOutcomes abilityOutcomes = new NpcAbilityOutcomes();

    /**
     * Retrieves the authored outcome string for the given ability result, delegating to
     * {@link NpcAbilityOutcomes#forAbility(Ability, boolean)}.
     */
    public Optional<String> abilityOutcome(Ability ability, boolean success) {
        return abilityOutcomes == null
                ? Optional.empty()
                : abilityOutcomes.forAbility(ability, success);
    }
}
