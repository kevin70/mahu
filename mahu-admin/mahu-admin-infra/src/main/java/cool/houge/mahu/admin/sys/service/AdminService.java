package cool.houge.mahu.admin.sys.service;

import com.google.common.base.Strings;
import com.password4j.Password;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.Status;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.dto.AdminQuery;
import cool.houge.mahu.admin.event.CollectProfileEvent;
import cool.houge.mahu.admin.sys.repository.AdminAccessLogRepository;
import cool.houge.mahu.admin.sys.repository.AdminAuthLogRepository;
import cool.houge.mahu.admin.sys.repository.AdminChangeLogRepository;
import cool.houge.mahu.admin.sys.repository.AdminRepository;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.Admin;
import cool.houge.mahu.entity.sys.AdminAccessLog;
import cool.houge.mahu.entity.sys.AdminAuthLog;
import cool.houge.mahu.entity.sys.AdminChangeLog;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Event;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 管理员
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class AdminService {

    private static final Logger log = LogManager.getLogger();

    final AdminRepository adminRepository;
    final AdminAccessLogRepository adminAccessLogRepository;
    final AdminAuthLogRepository adminAuthLogRepository;
    final AdminChangeLogRepository adminChangeLogRepository;
    final Event.Emitter<CollectProfileEvent> collectProfileEvent;
    final EntityBeanMapper beanMapper;

    /// 获取个人信息
    ///
    /// @param uid 用户 ID
    @Transactional
    public Profile getProfile(int uid) {
        var user = adminRepository.obtainById(uid);
        if (Status.ACTIVE.neq(user.getStatus())) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "帐号已被禁止");
        }

        var profile = new Profile();
        beanMapper.map(profile, user);
        collectProfileEvent.emit(new CollectProfileEvent(uid, profile));
        return profile;
    }

    /// 保存
    @Transactional
    public void save(Admin entity) {
        entity.setDeleted(false).setStatus(Status.ACTIVE.getCode());
        encryptPassword(entity);
        adminRepository.save(entity);
    }

    /// 更新
    @Transactional
    public void update(Admin entity) {
        encryptPassword(entity);
        adminRepository.update(entity);
    }

    /// 删除指定 ID 的职员
    @Transactional
    public void delete(int id) {
        adminRepository.delete(new Admin().setId(id));
    }

    /// 更新密码
    @Transactional
    public void updatePassword(Admin entity) {
        var dbEntity = obtainById(entity.getId());
        var matched = Password.check(entity.getOriginalPassword(), dbEntity.getPassword())
                .withArgon2();
        if (!matched) {
            throw new BizCodeException(BizCodes.FAILED_PRECONDITION, "原密码不匹配");
        }

        encryptPassword(entity);
        adminRepository.update(entity);
        log.debug("用户 {} 密码修改完成", entity.getId());
    }

    /// 查询指定 ID 的职员
    @Transactional(readOnly = true)
    public Admin obtainById(int id) {
        var entity = adminRepository.obtainById(id);
        log.debug("加载职员角色 {}", entity.getRoles());
        return entity;
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Admin> findPage(AdminQuery query, Page page) {
        return adminRepository.findPage(query, page);
    }

    /// 管理员访问日志分页查询
    @Transactional(readOnly = true)
    public PagedList<AdminAccessLog> pageAdminAccessLog(Integer adminId, Page page) {
        return adminAccessLogRepository.findPage(adminId, page);
    }

    /// 管理员认证日志分页查询
    @Transactional(readOnly = true)
    public PagedList<AdminAuthLog> pageAdminAuthLog(Integer adminId, Page page) {
        return adminAuthLogRepository.findPage(adminId, page);
    }

    /// 管理员操作日志分页查询
    @Transactional(readOnly = true)
    public PagedList<AdminChangeLog> pageAdminChangeLog(Integer adminId, Page page) {
        return adminChangeLogRepository.findPage(adminId, page);
    }

    void encryptPassword(Admin bean) {
        if (!Strings.isNullOrEmpty(bean.getPassword())) {
            var p = Password.hash(bean.getPassword()).addRandomSalt().withArgon2();
            bean.setPassword(p.getResult());
        }
    }
}
