package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import t1tanic.kingdomrpg.domain.character.enums.Ability;

import java.util.Optional;

/**
 * Per-NPC flavor text for each persuasion ability outcome.
 * <p>Each field is optional — define only the outcomes meaningful for this character.
 * {@link #forAbility(Ability, boolean)} retrieves the right string at runtime.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NpcAbilityOutcomes {

    @Column(name = "outcome_bribe_success",      length = 500) private String bribeSuccess;
    @Column(name = "outcome_bribe_failure",      length = 500) private String bribeFailure;
    @Column(name = "outcome_intimidate_success", length = 500) private String intimidateSuccess;
    @Column(name = "outcome_intimidate_failure", length = 500) private String intimidateFailure;
    @Column(name = "outcome_convince_success",   length = 500) private String convinceSuccess;
    @Column(name = "outcome_convince_failure",   length = 500) private String convinceFailure;
    @Column(name = "outcome_deceive_success",    length = 500) private String deceiveSuccess;
    @Column(name = "outcome_deceive_failure",    length = 500) private String deceiveFailure;
    @Column(name = "outcome_negotiate_success",  length = 500) private String negotiateSuccess;
    @Column(name = "outcome_negotiate_failure",  length = 500) private String negotiateFailure;
    @Column(name = "outcome_sense_success",      length = 500) private String senseSuccess;
    @Column(name = "outcome_sense_failure",      length = 500) private String senseFailure;

    /**
     * Returns the outcome string for a given ability and result, if one was defined.
     *
     * @param ability the persuasion ability that was attempted
     * @param success {@code true} for a passing roll, {@code false} for failure (includes fumbles)
     * @return the flavor text, or empty if this NPC has no specific reaction defined
     */
    public Optional<String> forAbility(Ability ability, boolean success) {
        String val = switch (ability) {
            case BRIBE        -> success ? bribeSuccess      : bribeFailure;
            case INTIMIDATE   -> success ? intimidateSuccess : intimidateFailure;
            case CONVINCE     -> success ? convinceSuccess   : convinceFailure;
            case DECEIVE      -> success ? deceiveSuccess    : deceiveFailure;
            case NEGOTIATE    -> success ? negotiateSuccess  : negotiateFailure;
            case SENSE_MOTIVE -> success ? senseSuccess      : senseFailure;
            default           -> null;
        };
        return Optional.ofNullable(val);
    }
}
