package cool.houge.mahu.admin.sys.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.shared.query.FeatureFlagQuery;
import cool.houge.mahu.shared.repository.sys.FeatureFlagRepository;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import lombok.AllArgsConstructor;

/// 功能开关服务
@Service.Singleton
@AllArgsConstructor
public class FeatureFlagService {

    private final EntityBeanMapper beanMapper;
    private final FeatureFlagRepository featureFlagRepository;

    /// 新建功能开关
    @Transactional
    public void create(FeatureFlag entity) {
        featureFlagRepository.save(entity);
    }

    /// 更新功能开关
    @Transactional
    public void update(FeatureFlag entity) {
        var dbEntity = obtainById(entity.getId());
        // if (dbEntity.isPreset()) {
        //     entity.setCode(null);
        // }
        beanMapper.map(dbEntity, entity);
        featureFlagRepository.update(dbEntity);
    }

    /// 查询指定功能开关
    @Transactional(readOnly = true)
    public FeatureFlag findById(int id) {
        return obtainById(id);
    }

    /// 分页查询功能开关
    @Transactional(readOnly = true)
    public PagedList<FeatureFlag> findPage(FeatureFlagQuery query, Page page) {
        return featureFlagRepository.findPage(query, page);
    }

    private FeatureFlag obtainById(int id) {
        var b = featureFlagRepository.findById(id);
        if (b == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到功能开关: %s", id);
        }
        return b;
    }
}
