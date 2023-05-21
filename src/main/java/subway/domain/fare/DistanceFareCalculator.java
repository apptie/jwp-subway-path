package subway.domain.fare;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public enum DistanceFareCalculator {

    BASE_FARE_AMOUNT(
            distance -> distance <= 10,
            ignoredDistance -> FareAmount.from(1_250L)
    ),
    ADDITIONAL_FARE_BY_TEN(
            distance -> 10 < distance && distance <= 50,
            DistanceFareCalculator::calculateTotalFareByTen
    ),
    ADDITIONAL_FARE_BY_FIFTY(
            distance -> distance > 50,
            DistanceFareCalculator::calculateTotalFareByFifty
    );

    private final Predicate<Integer> distanceChecker;
    private final Function<Integer, FareAmount> fareAmountCalculator;

    DistanceFareCalculator(
            final Predicate<Integer> distanceChecker,
            final Function<Integer, FareAmount> fareAmountCalculator
    ) {
        this.distanceChecker = distanceChecker;
        this.fareAmountCalculator = fareAmountCalculator;
    }

    private static final int DEFAULT_DISTANCE = 10;
    private static final long DEFAULT_FARE = 1_250L;

    private static final int ADDITIONAL_DISTANCE_PER_TEN = 10;
    private static final long ADDITIONAL_FARE_PER_TEN = 100L;
    private static final int DISTANCE_UNIT_PER_TEN = 5;

    private static final int ADDITIONAL_DISTANCE_PER_FIFTY = 50;
    private static final long ADDITIONAL_FARE_PER_FIFTY = 100L;
    private static final int DISTANCE_UNIT_PER_FIFTY = 8;

    private static FareAmount calculateTotalFareByTen(final int distance) {
        final long additionalFare = calculateAdditionalFare(
                distance - ADDITIONAL_DISTANCE_PER_TEN,
                ADDITIONAL_FARE_PER_TEN, DISTANCE_UNIT_PER_TEN
        );

        final long totalFare = DEFAULT_FARE + additionalFare;

        return FareAmount.from(totalFare);
    }

    private static FareAmount calculateTotalFareByFifty(final int distance) {
        final long additionalFareByTen = calculateAdditionalFare(
                ADDITIONAL_DISTANCE_PER_FIFTY - DEFAULT_DISTANCE,
                ADDITIONAL_FARE_PER_TEN,
                DISTANCE_UNIT_PER_TEN
        );
        final long additionalFareByFifty = calculateAdditionalFare(
                distance - ADDITIONAL_DISTANCE_PER_FIFTY,
                ADDITIONAL_FARE_PER_FIFTY,
                DISTANCE_UNIT_PER_FIFTY
        );
        final long totalFare = DEFAULT_FARE + additionalFareByTen + additionalFareByFifty;

        return FareAmount.from(totalFare);
    }

    private static long calculateAdditionalFare(final int distance, final long farePerUnit, final int distanceUnit) {
        return (((distance - 1) / distanceUnit) + 1) * farePerUnit;
    }

    public static FareAmount calculate(final int totalDistance) {
        return Arrays.stream(DistanceFareCalculator.values())
                .filter(distanceFareCalculator -> distanceFareCalculator.distanceChecker.test(totalDistance))
                .map(distanceFareCalculator -> distanceFareCalculator.fareAmountCalculator.apply(totalDistance))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("이동할 수 있는 거리가 아닙니다."));
    }
}
