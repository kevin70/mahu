package cool.houge.mahu.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.Region;
import cool.houge.mahu.entity.query.QRegion;
import io.ebean.Database;
import jakarta.inject.Singleton;

import java.util.List;

/// 行政区
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RegionRepository extends HBeanRepository<String, Region> {

    public RegionRepository(Database db) {
        super(Region.class, db);
    }

    /// 查询指定深度以下（包含）的行政区
    public List<Region> findDepthBound(int depth) {
        return new QRegion(db()).depth.le(depth).findList();
    }

    /// 指定指定父行政区代码的数据
    public List<Region> findByParentCode(String parentCode) {
        return new QRegion(db()).parentCode.eq(parentCode).findList();
    }
}
