package cool.houge.mahu.admin.system.repository;

import com.google.common.base.Strings;
import cool.houge.mahu.entity.system.Admin;
import cool.houge.mahu.entity.system.query.QAdmin;
import cool.houge.mahu.rsql.FilterItem;
import cool.houge.mahu.util.DataFilter;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.helidon.service.registry.Service.Singleton;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

/// 管理员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class AdminRepository extends HBeanRepository<Long, Admin> {

    public AdminRepository(Database db) {
        super(Admin.class, db);
    }

    public Admin obtainById(long id) {
        return findByIdOrEmpty(id)
                .orElseThrow(() -> new EntityNotFoundException(Strings.lenientFormat("未找到用户[id=%s]", id)));
    }

    public Admin findByUsername(String username) {
        return new QAdmin(db()).username.eq(username).findOne();
    }

    /// 分页查询
    public PagedList<Admin> findPage(DataFilter dataFilter) {
        var qb = new QAdmin(db());
        var filterItems = List.of(
                FilterItem.of(qb.createdAt),
                FilterItem.of(qb.updatedAt),
                FilterItem.of(qb.nickname),
                FilterItem.of(qb.status, Admin.Status::valueOf));

        super.apply(qb, dataFilter, filterItems);
        return qb.findPagedList();
    }
}
