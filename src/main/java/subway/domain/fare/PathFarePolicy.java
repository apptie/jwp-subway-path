package subway.domain.fare;

import java.util.List;
import subway.domain.path.Path;

public class PathFarePolicy implements FarePolicy {

    private final List<FarePolicy> farePolicies;

    public PathFarePolicy(final List<FarePolicy> farePolicies) {
        this.farePolicies = farePolicies;
    }

    @Override
    public FareAmount calculateFareAmount(final FareAmount fareAmount, final Path path) {
        FareAmount totalFareAmount = fareAmount;

        for (FarePolicy farePolicy : farePolicies) {
            totalFareAmount = farePolicy.calculateFareAmount(totalFareAmount, path);
        }

        return totalFareAmount;
    }
}
