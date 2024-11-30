package cool.houge.mahu.admin.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.Region;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 行政地区
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RegionRepository extends HBeanRepository<String, Region> {

    public RegionRepository(Database db) {
        super(Region.class, db);
    }
}
