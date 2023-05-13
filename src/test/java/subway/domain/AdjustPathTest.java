package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AdjustPathTest {

    private static final Station STATION = Station.from("잠실역");

    private AdjustPath adjustPath;

    @BeforeEach
    void setUp() {
        adjustPath = AdjustPath.create();
    }

    @Test
    void 인접한_역과_거리를_저장한다() {
        final Distance distance = Distance.from(10);

        assertDoesNotThrow(() -> adjustPath.add(STATION, distance, Direction.UP));
    }

    @Test
    void 인접한_역을_검색할_경우_거리를_가져온다() {
        // given
        final Distance distance = Distance.from(10);

        // when
        adjustPath.add(STATION, distance, Direction.UP);
        final PathInfo actual = adjustPath.findPathInfoByStation(STATION);

        // then
        assertThat(actual.getDistance().getDistance()).isEqualTo(10);
    }

    @Test
    void 인접하지_않은_역을_검색할_경우_예외가_발생한다() {
        assertThatThrownBy(() -> adjustPath.findPathInfoByStation(STATION))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인접하지 않은 역 입니다.");
    }

    @Test
    void 인접한_역을_삭제한다() {
        final Distance distance = Distance.from(10);
        adjustPath.add(STATION, distance, Direction.UP);

        adjustPath.delete(STATION);
    }
}
