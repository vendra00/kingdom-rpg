package t1tanic.kingdomrpg.engine.dice;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Enumerates standard polyhedral gaming dice types and provides algorithmic factories
 * for resolving random numeric roll simulations.
 * <p>Supports single or multi-die configurations, distinct physical advantage and disadvantage
 * roll conditions matching classic d20/D&D mechanics, and parsing standard string expressions.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public enum Dice {
    /** Four-sided die. */
    D4(4),
    /** Six-sided die. */
    D6(6),
    /** Eight-sided die. */
    D8(8),
    /** Ten-sided die. */
    D10(10),
    /** Twelve-sided die. */
    D12(12),
    /** Twenty-sided die. Standard for resolution and skill checks. */
    D20(20),
    /** One hundred-sided percentile die layout. */
    D100(100);

    private final int sides;

    Dice(int sides) { this.sides = sides; }
    /**
     * @return the number of physical faces available on this die type constant
     */
    public int sides() { return sides; }
    /**
     * Rolls this specific die a single time.
     *
     * @return a completed {@link DiceRoll} snapshot containing a single pseudo-random integer value
     */
    public DiceRoll roll() { return roll(1); }
    /**
     * Rolls this specific die multiple times and aggregates the collective individual result list.
     * <p>Utilizes {@link ThreadLocalRandom} to eliminate contention threads during simultaneous execution loops.</p>
     *
     * @param count the total amount of times to cast this die type instance
     * @return a consolidated {@link DiceRoll} record tracking individual values with zero initial base modifiers
     */
    public DiceRoll roll(int count) {
        var rng = ThreadLocalRandom.current();
        List<Integer> results = IntStream.range(0, count)
                .mapToObj(_ -> rng.nextInt(1, sides + 1))
                .toList();
        return new DiceRoll(this, results, 0);
    }
    /**
     * Rolls this specific die twice independently and keeps the highest singular generated result value.
     * Mimics classic D&D 5e mechanics for rolling with advantage.
     *
     * @return a completed {@link DiceRoll} packaging the maximum single result value
     */
    public DiceRoll withAdvantage() {
        var rng = ThreadLocalRandom.current();
        int a = rng.nextInt(1, sides + 1);
        int b = rng.nextInt(1, sides + 1);
        return new DiceRoll(this, List.of(Math.max(a, b)), 0);
    }
    /**
     * Rolls this specific die twice independently and keeps the lowest singular generated result value.
     * Mimics classic D&D 5e mechanics for rolling with disadvantage.
     *
     * @return a completed {@link DiceRoll} packaging the minimum single result value
     */
    public DiceRoll withDisadvantage() {
        var rng = ThreadLocalRandom.current();
        int a = rng.nextInt(1, sides + 1);
        int b = rng.nextInt(1, sides + 1);
        return new DiceRoll(this, List.of(Math.min(a, b)), 0);
    }
    /**
     * Parses standard compact dice notation string layout patterns and evaluates the execution rolls immediately.
     * <p>Expected syntax options: {@code "d20"}, {@code "2d6"}, or {@code "1d8"}.</p>
     * <p><b>Note:</b> This processing layer intentionally does not parse arithmetic modifier tails (e.g., "+3").
     * To append static modifier numbers, chain the returned record using {@link DiceRoll#plus(int)}.</p>
     *
     * @param notation the raw alphanumeric expression string representing the dice pool format
     * @return a completed calculated {@link DiceRoll} record tracking the matching parameters
     * @throws IllegalArgumentException if the delimiter is missing or numerical tokens fail to convert cleanly
     */
    public static DiceRoll roll(String notation) {
        String s = notation.trim().toLowerCase();
        int d = s.indexOf('d');
        if (d < 0) throw new IllegalArgumentException("Invalid notation: " + notation);
        int count     = d == 0 ? 1 : Integer.parseInt(s.substring(0, d));
        int sideCount = Integer.parseInt(s.substring(d + 1));
        return forSides(sideCount).roll(count);
    }
    /**
     * Maps an integer side configuration threshold back to its valid standard matching {@code Dice} enum constant.
     *
     * @param sides the number of faces to match against
     * @return the relevant matching {@code Dice} instance
     * @throws IllegalArgumentException if the side metric does not correspond to any standard polyhedral die type
     */
    public static Dice forSides(int sides) {
        for (Dice d : values()) {
            if (d.sides == sides) return d;
        }
        throw new IllegalArgumentException("No standard die with " + sides + " sides");
    }
}