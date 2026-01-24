package cool.houge.mahu.shared.repository.sys;

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
        return super.findPage(qb, page);
    }
}
