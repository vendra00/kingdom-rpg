package t1tanic.kingdomrpg.engine.dice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Immutable result of one or more dice rolls, optionally carrying a flat modifier.
 * Chain {@link #plus(int)} to attach an attribute bonus without mutating this object.
 */
public record DiceRoll(Dice dice, List<Integer> rolls, int modifier) {

    public DiceRoll {
        rolls = List.copyOf(rolls);
    }

    /** Sum of all individual rolls, before the modifier. */
    public int rawTotal() {
        return rolls.stream().mapToInt(Integer::intValue).sum();
    }

    /** Final result: raw total plus the modifier. */
    public int total() {
        return rawTotal() + modifier;
    }

    /** Return a new DiceRoll with an additional flat modifier (e.g. an ability bonus). */
    public DiceRoll plus(int mod) {
        return new DiceRoll(dice, rolls, modifier + mod);
    }

    /** True when a single d20 shows a natural 20. */
    public boolean isCritical() {
        return dice == Dice.D20 && rolls.size() == 1 && rolls.get(0) == 20;
    }

    /** True when a single d20 shows a natural 1. */
    public boolean isFumble() {
        return dice == Dice.D20 && rolls.size() == 1 && rolls.get(0) == 1;
    }

    /** Human-readable line suitable for terminal output. */
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

    @Override
    public String toString() {
        return format();
    }
}
