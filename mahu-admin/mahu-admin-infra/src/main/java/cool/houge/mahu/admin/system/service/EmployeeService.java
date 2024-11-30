package cool.houge.mahu.admin.system.service;

import com.google.common.base.Strings;
import com.password4j.Password;
import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.admin.bean.GeneralBeanMapper;
import cool.houge.mahu.admin.bean.Profile;
import cool.houge.mahu.admin.event.CollectProfileEvent;
import cool.houge.mahu.admin.system.repository.EmployeeRepository;
import cool.houge.mahu.common.DataFilter;
import cool.houge.mahu.entity.system.Employee;
import io.avaje.inject.events.Event;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// 职员
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    Event<CollectProfileEvent> collectProfileEvent;

    @Inject
    GeneralBeanMapper beanMapper;

    /// 获取个人信息
    ///
    /// @param uid 用户 ID
    @Transactional
    public Profile getProfile(long uid) {
        var user = employeeRepository.obtainById(uid);
        if (user.getStatus() != Employee.Status.ACTIVE) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "帐号已被禁止");
        }

        var profile = new Profile();
        beanMapper.map(profile, user);
        collectProfileEvent.fire(new CollectProfileEvent(uid, profile));
        return profile;
    }

    /// 保存
    @Transactional
    public void save(Employee entity) {
        entity.setDeleted(false).setStatus(Employee.Status.ACTIVE);
        encryptPassword(entity);
        employeeRepository.save(entity);
    }

    /// 更新
    @Transactional
    public void update(Employee entity) {
        encryptPassword(entity);
        employeeRepository.update(entity);
    }

    /// 删除指定 ID 的职员
    @Transactional
    public void delete(long id) {
        employeeRepository.delete(new Employee().setId(id));
    }

    /// 更新密码
    @Transactional
    public void updatePassword(Employee entity) {
        var dbEntity = obtainById(entity.getId());
        var matched = Password.check(entity.getOriginalPassword(), dbEntity.getPassword())
                .withArgon2();
        if (!matched) {
            throw new BizCodeException(BizCodes.FAILED_PRECONDITION, "原密码不匹配");
        }

        encryptPassword(entity);
        employeeRepository.update(entity);
        log.debug("用户 {} 密码修改完成", entity.getId());
    }

    /// 查询指定 ID 的职员
    @Transactional(readOnly = true)
    public Employee obtainById(long id) {
        var entity = employeeRepository.obtainById(id);
        log.debug("加载职员角色 {}", entity.getRoles());
        return entity;
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Employee> findPage(DataFilter dataFilter) {
        return employeeRepository.findPage(dataFilter);
    }

    void encryptPassword(Employee bean) {
        if (!Strings.isNullOrEmpty(bean.getPassword())) {
            var p = Password.hash(bean.getPassword()).addRandomSalt().withArgon2();
            bean.setPassword(p.getResult());
        }
    }
}
