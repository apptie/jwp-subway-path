package subway.domain.fare;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
class DistanceFareCalculatorTest {

    @Test
    void calculate_메소드는_거리가_9km_인_경우_기본_요금_1250을_반환한다() {
        final FareAmount actual = DistanceFareCalculator.calculate(9);

        assertThat(actual.getAmount()).isEqualTo(1_250L);
    }

    @Test
    void calculate_메소드는_거리가_12km_인_경우_추가_요금_1350을_반환한다() {
        final FareAmount actual = DistanceFareCalculator.calculate(12);

        assertThat(actual.getAmount()).isEqualTo(1_350L);
    }

    @Test
    void calculate_메소드는_거리가_12km_인_경우_추가_요금_1450을_반환한다() {
        final FareAmount actual = DistanceFareCalculator.calculate(16);

        assertThat(actual.getAmount()).isEqualTo(1450);
    }

    @Test
    void calculate_메소드는_거리가_58km_인_경우_추가_요금_2150을_반환한다() {
        final FareAmount actual = DistanceFareCalculator.calculate(58);

        assertThat(actual.getAmount()).isEqualTo(2_150L);
    }
}
