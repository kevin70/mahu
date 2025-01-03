package cool.houge.mahu.admin.system.repository;

import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.common.HBeanRepository;
import cool.houge.mahu.common.rsql.RSQLContext;
import cool.houge.mahu.entity.system.Department;
import cool.houge.mahu.entity.system.query.QDepartment;
import io.ebean.Database;
import io.ebean.PagedList;
import jakarta.inject.Singleton;

/// 部门
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DepartmentRepository extends HBeanRepository<Integer, Department> {

    public DepartmentRepository(Database db) {
        super(Department.class, db);
    }

    /// 分页查询
    ///
    /// **支持 RSQL 过滤的属性：**
    ///
    /// | 字段 | 数据类型 |
    /// | --- | ----- |
    /// | create_time | date-time |
    /// | update_time | date-time |
    /// | name | string |
    /// | ordering | int |
    public PagedList<Department> findPage(DataFilter filter) {
        var qb = new QDepartment(db());
        var rsqlCtx = RSQLContext.of(qb)
                .property("create_time", qb.createTime)
                .property("update_time", qb.updateTime)
                .property(qb.name)
                .property(qb.ordering);
        super.apply(filter, rsqlCtx);
        return qb.findPagedList();
    }
}
