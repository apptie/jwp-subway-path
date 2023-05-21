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
        stationDao.insert(StationEntity.from("1역"));
        stationDao.insert(StationEntity.from("2역"));
        stationDao.insert(StationEntity.from("3역"));
        stationDao.insert(StationEntity.from("4역"));
        stationDao.insert(StationEntity.from("5역"));
        lineDao.insert(LineEntity.of("1호선", "bg-red-500"));
    }
}
