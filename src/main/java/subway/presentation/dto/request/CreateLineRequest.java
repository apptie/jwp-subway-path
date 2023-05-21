package subway.presentation.dto.request;

import javax.validation.constraints.NotBlank;

public class CreateLineRequest {

    @NotBlank(message = "노선의 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "노선의 색상을 입력해주세요.")
    private String color;

    private CreateLineRequest() {
    }

    private CreateLineRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public static CreateLineRequest of(final String name, final String color) {
        return new CreateLineRequest(name, color);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
