package cool.houge.mahu.shared.repository.sys;

import com.google.common.base.Strings;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.entity.sys.query.QFeature;
import cool.houge.mahu.shared.query.FeatureQuery;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service;

/// 系统功能配置
@Service.Singleton
public class FeatureRepository extends HBeanRepository<Integer, Feature> {

    public FeatureRepository(Database db) {
        super(Feature.class, db);
    }

    /// 分页查询
    public PagedList<Feature> findPage(FeatureQuery query, Page page) {
        var qb = new QFeature(db());
        if (query.getStatusList() != null && !query.getStatusList().isEmpty()) {
            qb.status.in(query.getStatusList());
        }

        if (!Strings.isNullOrEmpty(query.getName())) {
            qb.name.icontains(query.getName());
        }
        return super.findPage(qb, page);
    }
}
