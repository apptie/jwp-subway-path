package subway.persistence.repository;

import org.springframework.stereotype.Repository;
import subway.domain.Station;
import subway.persistence.dao.SectionDao;
import subway.persistence.dao.StationDao;
import subway.persistence.entity.SectionEntity;
import subway.persistence.entity.StationEntity;

import java.util.List;

@Repository
public class StationRepository {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public StationRepository(final StationDao stationDao, final SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public Station insert(final Station station) {
        final StationEntity stationEntity = StationEntity.from(station);
        final StationEntity insertedStationEntity = stationDao.insert(stationEntity);

        return Station.of(insertedStationEntity.getId(), insertedStationEntity.getName());
    }

    public Station findById(final Long id) {
        return stationDao.findById(id).to();
    }

    public void deleteById(final Long id) {
        final List<SectionEntity> sectionEntities = sectionDao.findAllByStationId(id);

        if (!sectionEntities.isEmpty()) {
            throw new IllegalArgumentException("노선에 등록되어 있는 역입니다.");
        }

        stationDao.deleteById(id);
    }
}
