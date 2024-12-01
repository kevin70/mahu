package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.system.repository.RoleRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.Role;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RoleService {

    @Inject
    RoleRepository roleRepository;

    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Transactional
    public void update(Role role) {
        roleRepository.update(role);
    }

    @Transactional
    public void deleteById(Integer id) {
        roleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Role findById(Integer id) {
        return roleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public PagedList<Role> findPage(DataFilter filter) {
        return roleRepository.findPage(filter);
    }
}
