package subway.domain.fare;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import subway.domain.line.Line;
import subway.domain.path.Path;
import subway.domain.path.PathSections;
import subway.domain.path.graph.PathEdge;
import subway.domain.path.graph.PathEdges;
import subway.domain.section.Direction;
import subway.domain.section.Distance;
import subway.domain.station.Station;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
class PathFarePolicyTest {

    DistanceFarePolicy distanceFarePolicy;
    PathFarePolicy farePolicy;

    @BeforeEach
    void setUp() {
        distanceFarePolicy = mock(DistanceFarePolicy.class);
        farePolicy = new PathFarePolicy(List.of(distanceFarePolicy));
    }

    @Test
    void calculateFareAmount_메소드는_모든_요금_정책을_토대로_요금을_계산한다() {
        final Station sourceStation = Station.of(1L, "1역");
        final Station targetStation = Station.of(2L, "2역");
        final Line line = Line.of(1L, "1호선", "bg-red-500");
        line.createSection(sourceStation, targetStation, Distance.from(5), Direction.DOWN);
        final Path path = createPath(sourceStation, targetStation, line);
        final FareAmount actual = farePolicy.calculateFareAmount(FareAmount.from(0L), path);

        verify(distanceFarePolicy, times(1))
                .calculateFareAmount(any(FareAmount.class), any(Path.class));
    }

    private Path createPath(final Station sourceStation, final Station targetStation, final Line line) {
        final PathEdge pathEdge = PathEdge.of(sourceStation, targetStation, line);
        final PathEdges pathEdges = PathEdges.create();

        pathEdges.add(pathEdge);

        final PathSections pathSections = pathEdges.to();
        return Path.from(List.of(pathSections));
    }
}
