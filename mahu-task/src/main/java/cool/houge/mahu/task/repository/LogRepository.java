package cool.houge.mahu.task.repository;

import com.github.f4b6a3.ulid.Ulid;
import cool.houge.mahu.entity.log.BaseLog;
import io.ebean.BeanRepository;
import io.ebean.Database;
import jakarta.inject.Singleton;

import java.util.Collection;

/// 日志存储
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class LogRepository extends BeanRepository<String, BaseLog> {

    public LogRepository(Database db) {
        super(BaseLog.class, db);
    }

    @Override
    public void save(BaseLog bean) {
        if (bean.getId() == null) {
            bean.setId(Ulid.fast().toLowerCase());
        }
        super.save(bean);
    }

    @Override
    public int saveAll(Collection<BaseLog> beans) {
        for (BaseLog bean : beans) {
            if (bean.getId() == null) {
                bean.setId(Ulid.fast().toLowerCase());
            }
        }
        return super.saveAll(beans);
    }
}
