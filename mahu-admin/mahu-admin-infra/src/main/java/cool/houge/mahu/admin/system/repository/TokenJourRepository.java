package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.entity.system.TokenJour;
import io.ebean.Database;
import jakarta.inject.Singleton;

/// 访问令牌日志
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class TokenJourRepository extends HBeanRepository<String, TokenJour> {

    public TokenJourRepository(Database database) {
        super(TokenJour.class, database);
    }
}
