package t1tanic.kingdomrpg.engine.dice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An immutable record representing the result of one or more dice rolls, tracking individual rolls
 * and carrying an optional static numeric modifier.
 * <p>This class encapsulates the data needed to evaluate complex dice outcomes, support mechanical triggers
 * (like critical hits or fumbles), and generate formatted narrative string readouts. It is designed
 * to be non-mutating; adding bonuses is achieved by chaining the {@link #plus(int)} method.</p>
 *
 * @param dice     the standard polyhedral {@link Dice} configuration used for the roll
 * @param rolls    the unmodifiable {@link List} of individual integer results generated from each die face cast
 * @param modifier the flat integer bonus or penalty applied to the cumulative raw score total
 * @author t1tanic
 * @version 1.0
 */
public record DiceRoll(Dice dice, List<Integer> rolls, int modifier) {

    /**
     * Compact constructor designed to enforce record immutability defenses.
     * <p>Ensures that the internal listing of individual results is defensively shallow-copied to prevent
     * external array mutation loops.</p>
     */
    public DiceRoll {
        rolls = List.copyOf(rolls);
    }

    /**
     * Computes the mathematical sum of all individual dice rolls before evaluating any modifier shifts.
     *
     * @return the raw calculated total as an integer value
     */
    public int rawTotal() {
        return rolls.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Computes the absolute final evaluated result of this pool action block.
     * <p>Formula: {@code rawTotal() + modifier}</p>
     *
     * @return the final modified score total as an integer value
     */
    public int total() {
        return rawTotal() + modifier;
    }

    /**
     * Returns a new independent {@code DiceRoll} copy carrying an altered static flat modifier factor.
     * <p>Typically utilized to seamlessly chain skill capabilities or inject specialized attribute
     * adjustment modifiers (e.g. {@code roll.plus(attributes.modifier(Attribute.STRENGTH))}) without mutating
     * the existing snapshot.</p>
     *
     * @param mod the structural integer value change to additively apply to the existing modifier score
     * @return a fresh immutable {@code DiceRoll} instance carrying the updated compound modifier
     */
    public DiceRoll plus(int mod) {
        return new DiceRoll(dice, rolls, modifier + mod);
    }

    /**
     * Evaluates whether this action outcome constitutes a natural 20 critical validation trigger.
     * <p>Conditions met only when a singular {@link Dice#D20} configuration is used and its raw generated
     * face roll evaluates exactly to 20.</p>
     *
     * @return {@code true} if the result is a critical success; {@code false} otherwise
     */
    public boolean isCritical() {
        return dice == Dice.D20 && rolls.size() == 1 && rolls.get(0) == 20;
    }

    /**
     * Evaluates whether this action outcome constitutes a natural 1 critical failure fumble trigger.
     * <p>Conditions met only when a singular {@link Dice#D20} configuration is used and its raw generated
     * face roll evaluates exactly to 1.</p>
     *
     * @return {@code true} if the result is a critical failure; {@code false} otherwise
     */
    public boolean isFumble() {
        return dice == Dice.D20 && rolls.size() == 1 && rolls.get(0) == 1;
    }

    /**
     * Compiles a readable logging or terminal breakdown string formatting the math layout of the rolls.
     * <p>Examples of generated format output variations:</p>
     * <ul>
     * <li>{@code "d20 → 15 + 3 = 18"}</li>
     * <li>{@code "2d6 → [4, 5] = 9 - 1 = 8"}</li>
     * <li>{@code "d20 → 20  — CRITICAL HIT!"}</li>
     * </ul>
     *
     * @return a structured, human-readable {@link String} capturing the full dice operation breakdown
     */
    public String format() {
        StringBuilder sb = new StringBuilder();

        int count = rolls.size();
        sb.append(count == 1 ? "d" : count + "d").append(dice.sides());
        sb.append(" → ");

        if (count == 1) {
            sb.append(rolls.get(0));
        } else {
            sb.append(rolls.stream().map(Object::toString)
                          .collect(Collectors.joining(", ", "[", "]")));
            sb.append(" = ").append(rawTotal());
        }

        if (modifier != 0) {
            sb.append(modifier > 0 ? " + " : " - ").append(Math.abs(modifier));
            sb.append(" = ").append(total());
        }

        if (isCritical())    sb.append("  — CRITICAL HIT!");
        else if (isFumble()) sb.append("  — CRITICAL MISS!");

        return sb.toString();
    }

    /**
     * Overrides default string structural generation to output the specialized layout format text.
     *
     * @return identical to the output of {@link #format()}
     */
    @Override
    public String toString() {
        return format();
    }
}
