package cz.cuni.mff.simekjan.generator;

import java.time.LocalDate;
import java.util.SplittableRandom;

/**
 * Generates seeded values for the supported column datatypes.
 */
public class DataProvider {

    private final SplittableRandom random;

    public DataProvider(long seed) {
        random = new SplittableRandom(seed);
    }

    public String generateStringType() {
        return "value_" + random.nextInt(1_000_000);
    }

    public boolean generateBooleanType() {
        return random.nextBoolean();
    }

    public int generateIntegerType() {
        return random.nextInt(1_000_000);
    }

    public float generateFloatType() {
        return random.nextFloat(0F, 1_000F);
    }

    public LocalDate generateDateType() {
        return LocalDate.ofEpochDay(random.nextLong(0, 36_525));
    }
}
