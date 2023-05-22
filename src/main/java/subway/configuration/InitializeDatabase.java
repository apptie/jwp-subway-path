package subway.configuration;

import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import subway.persistence.dao.LineDao;
import subway.persistence.dao.StationDao;
import subway.persistence.entity.LineEntity;
import subway.persistence.entity.StationEntity;

@Profile("default")
@Component
public class InitializeDatabase {

    private final StationDao stationDao;
    private final LineDao lineDao;

    public InitializeDatabase(final StationDao stationDao, final LineDao lineDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @PostConstruct
    public void init() {
        insertStation("1역");
        insertStation("2역");
        insertStation("3역");
        insertStation("4역");
        insertStation("5역");
        insertLine("1호선", "bg-red-500");
    }

    private void insertStation(final String stationName) {
        if (!stationDao.existsByName(stationName)) {
            stationDao.insert(StationEntity.from(stationName));
        }
    }

    private void insertLine(final String lineName, final String lineColor) {
        if (!lineDao.existsByName(lineName)) {
            lineDao.insert(LineEntity.of(lineName, lineColor));
        }
    }
}
