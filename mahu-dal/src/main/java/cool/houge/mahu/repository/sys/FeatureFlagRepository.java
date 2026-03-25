package cool.houge.mahu.repository.sys;

import com.google.common.base.Strings;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.entity.sys.query.QFeatureFlag;
import cool.houge.mahu.query.sys.FeatureFlagQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;
import java.time.Instant;

/// 功能开关
@Service.Singleton
public class FeatureFlagRepository extends HBeanRepository<Integer, FeatureFlag> {

    public FeatureFlagRepository(Database db) {
        super(FeatureFlag.class, db);
    }

    /// 分页查询
    public PagedList<FeatureFlag> findPage(FeatureFlagQuery query, Page page) {
        var qb = new QFeatureFlag(db());

        if (query.getEnabled() != null) {
            qb.enabled.eq(query.getEnabled());
        }

        if (!Strings.isNullOrEmpty(query.getName())) {
            qb.name.icontains(query.getName());
        }

        if (!Strings.isNullOrEmpty(query.getCode())) {
            qb.code.icontains(query.getCode());
        }

        qb.ordering.desc();
        return super.findPage(qb, page);
    }

    /// 原子开启：仅当 enable_at <= now 时才执行更新。
    public boolean enableIfDue(int id, Instant now) {
        var qb = new QFeatureFlag(db());
        return qb.id.eq(id)
                        .enabled
                        .isFalse()
                        .enableAt
                        .isNotNull()
                        .enableAt
                        .le(now)
                        .asUpdate()
                        .set(qb.enabled, true)
                        .set(qb.enableAt, null)
                        .update()
                == 1;
    }

    /// 原子关闭：仅当 disable_at <= now 时才执行更新。
    public boolean disableIfDue(int id, Instant now) {
        var qb = new QFeatureFlag(db());
        return qb.id.eq(id)
                        .enabled
                        .isTrue()
                        .disableAt
                        .isNotNull()
                        .disableAt
                        .le(now)
                        .asUpdate()
                        .set(qb.enabled, false)
                        .set(qb.disableAt, null)
                        .update()
                == 1;
    }
}
