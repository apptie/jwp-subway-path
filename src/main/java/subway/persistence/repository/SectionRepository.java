package subway.persistence.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;
import subway.domain.AdjustPath;
import subway.domain.Direction;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.PathInfo;
import subway.domain.Station;
import subway.persistence.dao.SectionDao;
import subway.persistence.dao.StationDao;
import subway.persistence.entity.SectionEntity;
import subway.persistence.entity.SectionEntity.Builder;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void insert(final Line line) {
        sectionDao.deleteByLineId(line.getId());

        final List<Station> stations = line.findStationsByOrdered();
        final List<SectionEntity> sectionEntities = new ArrayList<>();

        for (Station sourceStation : stations) {
            sectionEntities.addAll(convert(sourceStation, line.getId()));
        }
        sectionDao.insertAll(sectionEntities);
    }

    private List<SectionEntity> convert(final Station sourceStation, final Long lineId) {
        final List<SectionEntity> sectionEntities = new ArrayList<>();
        final AdjustPath adjustPath = sourceStation.getAdjustPath();
        final List<Station> relationStations = adjustPath.findAllStation();

        for (Station targetStation : relationStations) {
            final PathInfo pathInfo = adjustPath.findPathInfoByStation(targetStation);

            if (pathInfo.matchesByDirection(Direction.UP)) {
                continue ;
            }

            final SectionEntity sectionEntity = Builder.builder()
                    .lineId(lineId)
                    .upStationId(sourceStation.getId())
                    .downStationId(targetStation.getId())
                    .distance(pathInfo.getDistance().getDistance())
                    .build();
            sectionEntities.add(sectionEntity);
        }
        return sectionEntities;
    }

    private boolean isUpEnd(final List<SectionEntity> sectionEntities, final Long stationId) {
        return sectionEntities.stream()
                .noneMatch(sectionEntity -> sectionEntity.containsDownStationId(stationId));
    }

    private SectionEntity findSectionEntityByUpStationId(final List<SectionEntity> sectionEntities, final Long upStationId) {
        return sectionEntities.stream()
                .filter(sectionEntity -> sectionEntity.containsUpStationId(upStationId)).findAny().orElseThrow();
    }

    public void findAllByLine(final Line line) {
        final List<SectionEntity> sectionEntities = sectionDao.findAllByLineId(line.getId());

        if (sectionEntities.isEmpty()) {
            return ;
        }
        Set<Long> stationIds = new HashSet<>();

        for (SectionEntity sectionEntity : sectionEntities) {
            stationIds.add(sectionEntity.getUpStationId());
            stationIds.add(sectionEntity.getDownStationId());
        }

        Long upStationId = stationIds.stream()
                .filter(id -> isUpEnd(sectionEntities, id))
                .findAny()
                .orElseThrow();

        int count = 0;

        while (count++ < 1) {
            final SectionEntity targetSectionEntity = findSectionEntityByUpStationId(sectionEntities, upStationId);
            final Station upStation = stationDao.findById(targetSectionEntity.getUpStationId()).orElseThrow().to();
            final Station downStation = stationDao.findById(targetSectionEntity.getDownStationId()).orElseThrow().to();
            line.initialStations(upStation, downStation, Distance.from(targetSectionEntity.getDistance()));
            upStationId = targetSectionEntity.getDownStationId();
        }

        if (count == stationIds.size()) {
            return;
        }

        while (count++ < stationIds.size()) {
            final SectionEntity targetSectionEntity = findSectionEntityByUpStationId(sectionEntities, upStationId);
            final Station upStation = stationDao.findById(targetSectionEntity.getUpStationId()).orElseThrow().to();
            final Station downStation = stationDao.findById(targetSectionEntity.getDownStationId()).orElseThrow().to();
            line.addEndStation(upStation, downStation, Distance.from(targetSectionEntity.getDistance()));
            upStationId = targetSectionEntity.getDownStationId();
        }
    }

    private Long findStationByEnd(final List<SectionEntity> sectionEntities, final Long targetUpStationId) {
        final SectionEntity sectionEntity = sectionEntities.stream()
                .filter(targetSectionEntity -> targetSectionEntity.containsUpStationId(targetUpStationId))
                .findAny().orElseThrow();

        return sectionEntity.getDownStationId();
    }

    private Distance findDistanceByUpAndDownStationId(final List<SectionEntity> sectionEntities,
            final Long upStationId, final Long downStationId) {
        final SectionEntity findSection = sectionEntities.stream()
                .filter(sectionEntity -> sectionEntity.matchesByUpAndDownStationId(upStationId, downStationId))
                .findAny()
                .orElseThrow();

        return Distance.from(findSection.getDistance());
    }
}
