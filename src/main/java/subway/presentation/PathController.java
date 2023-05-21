package subway.presentation;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.application.dto.ShortestPathInfoDto;
import subway.presentation.dto.request.FindShortestPathRequest;
import subway.presentation.dto.response.ShortestPathInfoResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/shortest")
    public ResponseEntity<ShortestPathInfoResponse> findShortestPathAndFare(
            @ModelAttribute @Valid FindShortestPathRequest request
    ) {
        final ShortestPathInfoDto dto = pathService.findShortestPathInfo(
                request.getSourceStationId(),
                request.getTargetStationId()
        );

        return ResponseEntity.ok(ShortestPathInfoResponse.from(dto));
    }
}
