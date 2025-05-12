package cool.houge.mahu.admin.system.service;

import cool.houge.mahu.admin.shared.SharedToolService;
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

    private final RoleRepository roleRepository;
    private final SharedToolService toolService;

    @Inject
    public RoleService(RoleRepository roleRepository, SharedToolService toolService) {
        this.roleRepository = roleRepository;
        this.toolService = toolService;
    }

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
    public PagedList<Role> findPage(DataFilter dataFilter) {
        var plist = roleRepository.findPage(dataFilter);
        return toolService.wrap(plist, dataFilter);
    }
}
