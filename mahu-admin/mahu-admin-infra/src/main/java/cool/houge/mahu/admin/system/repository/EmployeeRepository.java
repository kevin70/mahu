package cool.houge.mahu.admin.system.repository;

import com.google.common.base.Strings;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.Employee;
import cool.houge.mahu.entity.system.query.QEmployee;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityNotFoundException;

/// 职员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class EmployeeRepository extends HBeanRepository<Long, Employee> {

    public EmployeeRepository(Database db) {
        super(Employee.class, db);
    }

    public Employee obtainById(long id) {
        return findByIdOrEmpty(id)
                .orElseThrow(() -> new EntityNotFoundException(Strings.lenientFormat("未找到用户[id=%s]", id)));
    }

    public Employee findByUsername(String username) {
        return new QEmployee(db()).username.eq(username).findOne();
    }

    /// 分页查询
    public PagedList<Employee> findPage(DataFilter dataFilter) {
        var qb = new QEmployee(db());
        apply(
                dataFilter,
                RSQLContext.of(qb)
                        .property("created_at", qb.createdAt)
                        .property("updated_at", qb.updatedAt)
                        .property(qb.nickname)
                        .property(qb.status, Employee.Status::valueOf)
                        .property("department_id", qb.department.id)
                //
                );

        return qb.findPagedList();
    }
}
