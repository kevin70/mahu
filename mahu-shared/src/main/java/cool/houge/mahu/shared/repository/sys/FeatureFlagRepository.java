package cool.houge.mahu.shared.repository.sys;

import com.google.common.base.Strings;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.entity.sys.query.QFeatureFlag;
import cool.houge.mahu.shared.query.FeatureFlagQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;

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

        return super.findPage(qb, page);
    }
}

