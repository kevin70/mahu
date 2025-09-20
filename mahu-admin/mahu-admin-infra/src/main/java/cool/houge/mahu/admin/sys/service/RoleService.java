package cool.houge.mahu.admin.sys.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.admin.sys.repository.RoleRepository;
import cool.houge.mahu.admin.entity.Role;
import cool.houge.mahu.domain.DataFilter;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 角色
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class RoleService {

    private static final Logger log = LogManager.getLogger();

    private final EntityBeanMapper beanMapper;
    private final RoleRepository roleRepository;

    @Transactional
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Transactional
    public void update(Role role) {
        var dbEntity = this.findById(role.getId());
        beanMapper.map(dbEntity, role);
        roleRepository.update(dbEntity);
        log.debug("系统角色[{}]更新完成", role.getId());
    }

    @Transactional
    public void deleteById(Integer id) {
        var dbEntity = this.findById(id);
        roleRepository.delete(dbEntity);
        log.debug("系统角色[{}]删除完成", id);
    }

    @Transactional(readOnly = true)
    public Role findById(Integer id) {
        var bean = roleRepository.findById(id);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到系统角色[" + id + "]");
        }
        return bean;
    }

    @Transactional(readOnly = true)
    public PagedList<Role> findPage(DataFilter dataFilter) {
        return roleRepository.findPage(dataFilter);
    }
}
