package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.dto.CreationLineDto;
import subway.domain.Line;
import subway.persistence.repository.LineRepository;
import subway.persistence.repository.SectionRepository;
import subway.ui.dto.request.CreationLineRequest;
import subway.ui.dto.response.ReadLineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public LineService(final LineRepository lineRepository, final SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    public CreationLineDto saveLine(final CreationLineRequest request) {
        final Line line = Line.of(request.getName(), request.getColor());
        final Line persistLine = lineRepository.insert(line);

        return CreationLineDto.from(persistLine);
    }

    public List<ReadLineResponse> findAllLine() {
        final List<Line> persistLines = lineRepository.findAll();

        for (Line persistLine : persistLines) {
            sectionRepository.findAllByLine(persistLine);
        }

        return persistLines.stream()
                .map(ReadLineResponse::of)
                .collect(Collectors.toList());
    }

    public ReadLineResponse findLineById(final Long id) {
        final Line line = lineRepository.findById(id);
        sectionRepository.findAllByLine(line);

        return ReadLineResponse.of(line);
    }

    public void deleteLineById(final Long id) {
        lineRepository.deleteById(id);
    }
}
