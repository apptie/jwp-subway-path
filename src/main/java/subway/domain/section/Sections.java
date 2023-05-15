package subway.domain.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import subway.domain.Direction;
import subway.domain.Distance;
import subway.domain.station.Station;

public class Sections {

    private static final int MINIMUM_SECTION_SIZE = 2;
    private final Map<Station, Section> sections;

    public Sections(final Map<Station, Section> sections) {
        this.sections = sections;
    }

    public static Sections create() {
        return new Sections(new HashMap<>());
    }

    public static Sections of(final Map<Station, Section> sections) {
        return new Sections(sections);
    }

    public void addSection(
            final Station sourceStation,
            final Station targetStation,
            final Distance distance,
            final Direction direction
    ) {
        if (sections.isEmpty()) {
            initialSection(sourceStation, targetStation, distance, direction);
            return ;
        }
        addStation(sourceStation, targetStation, distance, direction);
    }

    private void addStation(
            final Station sourceStation,
            final Station targetStation,
            final Distance distance,
            final Direction direction
    ) {
        validateStation(sourceStation, targetStation);

        final Section adjustPath = sections.get(sourceStation);

        adjustPath.findStationByDirection(direction).ifPresentOrElse(
                station -> addSectionToMiddleStation(sourceStation, targetStation, distance, station),
                () -> addSectionToTerminalStation(sourceStation, targetStation, distance, direction)
        );
    }

    private void validateStation(final Station sourceStation, final Station targetStation) {
        if (!isRegisterStation(sourceStation)) {
            throw new IllegalArgumentException("지정한 기준 역은 등록되어 있지 않은 역입니다.");
        }
        if (isRegisterStation(targetStation)) {
            throw new IllegalArgumentException("이미 등록된 역입니다.");
        }
    }

    private void addSectionToTerminalStation(
            final Station sourceStation,
            final Station targetStation,
            final Distance distance,
            final Direction direction
    ) {
        final Section adjustPath = sections.get(sourceStation);
        adjustPath.add(targetStation, distance, direction);

        final Section targetStationSection = Section.create();
        targetStationSection.add(sourceStation, distance, direction.reverse());
        sections.put(targetStation, targetStationSection);
    }

    private void addSectionToMiddleStation(
            final Station sourceStation,
            final Station targetStation,
            final Distance distance,
            final Station existsStation
    ) {
        final Section sourceStationSection = sections.get(sourceStation);
        final Section existsStationSection = sections.get(existsStation);
        final Section targetStationSection = Section.create();

        final Direction direction = sourceStationSection.findDirectionByStation(existsStation);
        final Distance middleDistance = sourceStationSection.calculateMiddleDistance(existsStation, distance);

        sourceStationSection.add(targetStation, distance, direction);
        targetStationSection.add(sourceStation, distance, direction.reverse());

        existsStationSection.add(targetStation, middleDistance, direction.reverse());
        targetStationSection.add(existsStation, middleDistance, direction);

        sourceStationSection.delete(existsStation);
        existsStationSection.delete(sourceStation);
        sections.put(targetStation, targetStationSection);
    }

    private void initialSection(
            final Station sourceStation,
            final Station targetStation,
            final Distance distance,
            final Direction direction) {
        final Section sourceStationSection = Section.create();
        final Section targetStationSection = Section.create();

        sourceStationSection.add(targetStation, distance, direction);
        targetStationSection.add(sourceStation, distance, direction.reverse());
        sections.put(sourceStation, sourceStationSection);
        sections.put(targetStation, targetStationSection);
    }

    public void removeStation(final Station targetStation) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("해당 역은 구간이 존재하지 않습니다.");
        }

        if (!isRegisterStation(targetStation)) {
            throw new IllegalArgumentException("해당 역은 노선에 등록되어 있지 않습니다.");
        }

        if (sections.values().size() == MINIMUM_SECTION_SIZE) {
            sections.clear();
            return ;
        }

        final Section adjustPath = sections.get(targetStation);

        if (adjustPath.isTerminalStation()) {
            removeSectionToTerminalStation(targetStation);
            return ;
        }

        removeSectionToMiddleStation(targetStation);
    }

    private boolean isRegisterStation(final Station targetStation) {
        return sections.containsKey(targetStation);
    }

    private void removeSectionToMiddleStation(final Station targetStation) {
        final Section adjustPath = sections.get(targetStation);
        final Station upStation = adjustPath.findStationByDirection(Direction.UP)
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 종점역 입니다."));
        final Station downStation = adjustPath.findStationByDirection(Direction.DOWN)
                .orElseThrow(() -> new IllegalArgumentException("해당 역은 종점역 입니다."));

        removeSectionInfo(targetStation, upStation, downStation);
        sections.remove(targetStation);
    }

    private void removeSectionInfo(final Station targetStation, final Station upStation, final Station downStation) {
        final Section adjustPath = sections.get(targetStation);
        final Distance upStationDistance = adjustPath.findDistanceByStation(upStation);
        final Distance downStationDistance = adjustPath.findDistanceByStation(downStation);
        final Distance distance = upStationDistance.add(downStationDistance);

        final Section upStationSection = sections.get(upStation);
        upStationSection.delete(targetStation);
        upStationSection.add(downStation, distance, Direction.DOWN);

        final Section downStationSection = sections.get(downStation);
        downStationSection.delete(targetStation);
        downStationSection.add(upStation, distance, Direction.UP);
    }

    private void removeSectionToTerminalStation(final Station targetStation) {
        final Section adjustPath = sections.get(targetStation);
        final List<Station> stations = adjustPath.findAllStation();

        for (Station station : stations) {
            validateConnected(station);
            sections.get(station).delete(targetStation);
        }

        sections.remove(targetStation);
    }

    private void validateConnected(final Station station) {
        if (!sections.containsKey(station)) {
            throw new IllegalArgumentException("해당 역은 연결되지 않은 역입니다.");
        }
    }

    public List<Station> findStationsByOrdered() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        final Queue<Station> queue = new LinkedList<>();
        final Set<Station> visited = new LinkedHashSet<>();

        final Station upStation = findStartStation();
        queue.add(upStation);
        visited.add(upStation);

        while (!queue.isEmpty()) {
            final Station nowStation = queue.poll();
            for (final Station nextStation : sections.get(nowStation).findAllStation()) {
                if (!visited.contains(nextStation)) {
                    queue.add(nextStation);
                    visited.add(nextStation);
                }
            }
        }

        return new ArrayList<>(visited);
    }

    private Station findStartStation() {
        return sections.keySet().stream()
                .filter(station -> sections.get(station).isTerminalStation())
                .filter(station -> sections.get(station).findEndStationPathDirection().matches(Direction.DOWN))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("아직 노선에 역이 등록되지 않았습니다."));
    }

    public Map<Station, Section> sections() {
        return new HashMap<>(sections);
    }
}
