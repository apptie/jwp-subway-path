package subway.presentation.dto.request;

import javax.validation.constraints.NotBlank;

public class CreateStationRequest {

    @NotBlank(message = "역의 이름을 입력해주세요.")
    private String name;

    private CreateStationRequest() {
    }

    private CreateStationRequest(final String name) {
        this.name = name;
    }

    public static CreateStationRequest from(final String name) {
        return new CreateStationRequest(name);
    }

    public String getName() {
        return name;
    }
}
