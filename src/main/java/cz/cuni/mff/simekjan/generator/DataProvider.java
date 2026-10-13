package cz.cuni.mff.simekjan.generator;

import java.time.LocalDate;
import java.util.SplittableRandom;

public class DataProvider {

    private final SplittableRandom random;

    public DataProvider(long seed) {
        random = new SplittableRandom(seed);
    }

    public String nextFirstName() {
        return DataPools.FIRST_NAMES.get(
            random.nextInt(DataPools.FIRST_NAMES.size())
        );
    }

    public String nextLastName() {
        return DataPools.LAST_NAMES.get(
            random.nextInt(DataPools.LAST_NAMES.size())
        );
    }

    public String nextCity() {
        return DataPools.CITIES.get(
            random.nextInt(DataPools.CITIES.size())
        );
    }

    public String nextCompanyName() {
        return DataPools.COMPANY_NAMES.get(
            random.nextInt(DataPools.COMPANY_NAMES.size())
        );
    }

    public String nextProductName() {
        return DataPools.PRODUCT_NAMES.get(
            random.nextInt(DataPools.PRODUCT_NAMES.size())
        );
    }

    public String nextCategory() {
        return DataPools.PRODUCT_CATEGORIES.get(
            random.nextInt(DataPools.PRODUCT_CATEGORIES.size())
        );
    }

    public Float nextPrice() {
        // Try to round to two decimals (Does not work now due to Floats being store as they are !!!!)
        return Math.round(random.nextFloat(5F, 500F) * 100F) / 100F;
    }

    public LocalDate nextHireDate() {

        int year = random.nextInt(2011, 2026);
        int day = random.nextInt(1, 365);

        return LocalDate.ofYearDay(year, day);
    }

    public LocalDate nextOrderDate() {

        int year = random.nextInt(2011, 2026);
        int day = random.nextInt(1, 365);

        return LocalDate.ofYearDay(year, day);
    }

    public LocalDate nextBirthDate() {

        int year = random.nextInt(1960, 2005);
        int day = random.nextInt(1, 365);

        return LocalDate.ofYearDay(year, day);
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public int nextInt(int min, int max) {
        return random.nextInt(min, max);
    }
}
