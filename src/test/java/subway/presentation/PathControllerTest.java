package subway.presentation;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import subway.application.PathService;
import subway.application.dto.ShortestPathInfoDto;
import subway.application.dto.ShortestPathsDto;
import subway.domain.fare.FareAmount;
import subway.domain.line.Line;
import subway.domain.path.Path;
import subway.domain.path.PathSections;
import subway.domain.path.graph.PathEdge;
import subway.domain.path.graph.PathEdges;
import subway.domain.section.Direction;
import subway.domain.section.Distance;
import subway.domain.station.Station;
import subway.exception.GlobalExceptionHandler;

@WebMvcTest(controllers = PathController.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
class PathControllerTest {

    @MockBean
    PathService pathService;

    @Autowired
    PathController pathController;

    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pathController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();
    }

    @Test
    void findShortestPathAndFare_메소드는_출발_역과_도착_역의_id를_전달하면_최단_경로와_요금을_반환한다()
            throws Exception {
        final Station sourceStation = Station.of(1L, "1역");
        final Station targetStation = Station.of(2L, "2역");
        final Line line = Line.of(1L, "1호선", "bg-red-500");
        line.createSection(sourceStation, targetStation, Distance.from(5), Direction.DOWN);
        final Path path = createPath(sourceStation, targetStation, line);
        final ShortestPathsDto shortestPathsDto = ShortestPathsDto.from(path);
        final FareAmount fareAmount = FareAmount.from(1_250L);
        final ShortestPathInfoDto shortestPathInfoDto = ShortestPathInfoDto.of(shortestPathsDto, fareAmount);
        given(pathService.findShortestPathInfo(anyLong(), anyLong())).willReturn(shortestPathInfoDto);

        mockMvc.perform(get("/paths/shortest")
                        .queryParam("sourceStationId", sourceStation.getId().toString())
                        .queryParam("targetStationId", sourceStation.getId().toString())
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.paths[0].stations[0].id", is(sourceStation.getId()), Long.class),
                        jsonPath("$.paths[0].stations[0].name", is(sourceStation.getName())),
                        jsonPath("$.paths[0].stations[1].id", is(targetStation.getId()), Long.class),
                        jsonPath("$.paths[0].stations[1].name", is(targetStation.getName())),
                        jsonPath("$.paths[0].pathDistance", is(5)),
                        jsonPath("$.totalDistance", is(5)),
                        jsonPath("$.fare", is(1_250L), Long.class)
                );
    }

    private Path createPath(final Station sourceStation, final Station targetStation, final Line line) {
        final PathEdge pathEdge = PathEdge.of(sourceStation, targetStation, line);
        final PathEdges pathEdges = PathEdges.create();

        pathEdges.add(pathEdge);

        final PathSections pathSections = pathEdges.to();
        return Path.from(List.of(pathSections));
    }
}
