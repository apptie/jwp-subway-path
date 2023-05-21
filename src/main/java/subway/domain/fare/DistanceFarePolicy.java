package subway.domain.fare;

import subway.domain.path.Path;

public class DistanceFarePolicy implements FarePolicy{

    @Override
    public FareAmount calculateFareAmount(final FareAmount fareAmount, final Path path) {
        final int totalDistance = path.calculateTotalDistance();

        return fareAmount.plus(DistanceFareCalculator.calculate(totalDistance));
    }
}
