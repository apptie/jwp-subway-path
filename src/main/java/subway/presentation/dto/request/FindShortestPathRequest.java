package subway.presentation.dto.request;

import javax.validation.constraints.Positive;

public class FindShortestPathRequest {

    @Positive(message = "역의 식별자는 양수여야 합니다.")
    private Long sourceStationId;

    @Positive(message = "역의 식별자는 양수여야 합니다.")
    private Long targetStationId;

    private FindShortestPathRequest() {
    }

    public FindShortestPathRequest(final Long sourceStationId, final Long targetStationId) {
        this.sourceStationId = sourceStationId;
        this.targetStationId = targetStationId;
    }

    public Long getSourceStationId() {
        return sourceStationId;
    }

    public Long getTargetStationId() {
        return targetStationId;
    }
}
