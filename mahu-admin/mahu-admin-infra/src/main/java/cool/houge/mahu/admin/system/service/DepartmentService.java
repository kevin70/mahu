package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.shared.SharedToolService;
import cool.houge.mahu.admin.system.repository.DepartmentRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.Department;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 部门
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class DepartmentService {

    private static final Logger log = LogManager.getLogger(DepartmentService.class);

    @Inject
    DepartmentRepository departmentRepository;

    @Inject
    SharedToolService toolService;

    /// 新增部门
    @Transactional
    public void save(Department department) {
        departmentRepository.save(department);
    }

    /// 更新部门
    @Transactional
    public void update(Department department) {
        departmentRepository.update(department);
    }

    /// 删除部门
    @Transactional
    public void delete(Department department) {
        departmentRepository.delete(department);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Department> findPage(DataFilter dataFilter) {
        var plist = departmentRepository.findPage(dataFilter);
        for (Department department : plist.getList()) {
            log.debug("load parent department: {}", department.getParent());
        }
        return toolService.wrap(plist, dataFilter);
    }

    /// 查询指定 ID 的部门
    @Transactional(readOnly = true)
    public Department findById(Integer id) {
        return departmentRepository.findById(id);
    }
}
