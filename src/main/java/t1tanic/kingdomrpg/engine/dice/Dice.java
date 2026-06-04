package t1tanic.kingdomrpg.engine.dice;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public enum Dice {

    D4(4), D6(6), D8(8), D10(10), D12(12), D20(20), D100(100);

    private final int sides;

    Dice(int sides) { this.sides = sides; }

    public int sides() { return sides; }

    /** Roll this die once. */
    public DiceRoll roll() { return roll(1); }

    /** Roll this die {@code count} times and collect all results. */
    public DiceRoll roll(int count) {
        var rng = ThreadLocalRandom.current();
        List<Integer> results = IntStream.range(0, count)
                .mapToObj(_ -> rng.nextInt(1, sides + 1))
                .toList();
        return new DiceRoll(this, results, 0);
    }

    /** Roll twice, keep the higher result (D&D advantage). */
    public DiceRoll withAdvantage() {
        var rng = ThreadLocalRandom.current();
        int a = rng.nextInt(1, sides + 1);
        int b = rng.nextInt(1, sides + 1);
        return new DiceRoll(this, List.of(Math.max(a, b)), 0);
    }

    /** Roll twice, keep the lower result (D&D disadvantage). */
    public DiceRoll withDisadvantage() {
        var rng = ThreadLocalRandom.current();
        int a = rng.nextInt(1, sides + 1);
        int b = rng.nextInt(1, sides + 1);
        return new DiceRoll(this, List.of(Math.min(a, b)), 0);
    }

    /**
     * Parse compact notation and roll immediately: "d20", "2d6", "1d8".
     * Does not handle modifiers — chain {@link DiceRoll#plus(int)} for those.
     */
    public static DiceRoll roll(String notation) {
        String s = notation.trim().toLowerCase();
        int d = s.indexOf('d');
        if (d < 0) throw new IllegalArgumentException("Invalid notation: " + notation);
        int count     = d == 0 ? 1 : Integer.parseInt(s.substring(0, d));
        int sideCount = Integer.parseInt(s.substring(d + 1));
        return forSides(sideCount).roll(count);
    }

    /** Look up the die with the given number of sides. */
    public static Dice forSides(int sides) {
        for (Dice d : values()) {
            if (d.sides == sides) return d;
        }
        throw new IllegalArgumentException("No standard die with " + sides + " sides");
    }
}
