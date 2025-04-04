package cool.houge.mahu.system.repository;

import cool.houge.mahu.common.HBeanRepository;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 获取访问令牌记录
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenJourRepository extends HBeanRepository<String, TokenJour> {

    public TokenJourRepository(Database db) {
        super(TokenJour.class, db);
    }
}
