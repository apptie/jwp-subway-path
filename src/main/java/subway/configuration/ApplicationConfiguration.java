package subway.configuration;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import subway.application.JGraphtShortestPathCalculator;
import subway.domain.fare.DistanceFarePolicy;
import subway.domain.fare.FarePolicy;
import subway.domain.fare.PathFarePolicy;
import subway.domain.path.ShortestPathCalculator;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public FarePolicy farePolicy() {
        return new PathFarePolicy(
                List.of(new DistanceFarePolicy())
        );
    }

    @Bean
    public ShortestPathCalculator shortestPathCalculator() {
        return new JGraphtShortestPathCalculator();
    }
}
