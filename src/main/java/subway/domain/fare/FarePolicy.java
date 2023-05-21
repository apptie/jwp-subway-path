package subway.domain.fare;

import subway.domain.path.Path;

public interface FarePolicy {

    FareAmount calculateFareAmount(final FareAmount fareAmount, final Path path);
}
