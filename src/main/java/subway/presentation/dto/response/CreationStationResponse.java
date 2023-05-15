package subway.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import subway.application.dto.CreationStationDto;

public class CreationStationResponse {

    private Long id;
    private String name;

    @JsonCreator
    public CreationStationResponse(@JsonProperty(value = "id") final Long id,
                                   @JsonProperty(value = "name") final String name) {
        this.id = id;
        this.name = name;
    }

    public static CreationStationResponse from(final CreationStationDto stationDto) {
        return new CreationStationResponse(stationDto.getId(), stationDto.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
