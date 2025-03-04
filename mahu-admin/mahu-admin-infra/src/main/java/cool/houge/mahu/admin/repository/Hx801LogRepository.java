package cool.houge.mahu.admin.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.Hx801Log;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// HX801 日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class Hx801LogRepository extends HBeanRepository<String, Hx801Log> {

    public Hx801LogRepository(Database db) {
        super(Hx801Log.class, db);
    }
}
