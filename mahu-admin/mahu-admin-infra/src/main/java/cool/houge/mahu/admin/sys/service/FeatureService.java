package cool.houge.mahu.admin.sys.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.domain.DataFilter;
import cool.houge.mahu.entity.sys.Feature;
import cool.houge.mahu.shared.repository.sys.FeatureRepository;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import lombok.AllArgsConstructor;

/// 系统特征服务
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class FeatureService {

    private final EntityBeanMapper beanMapper;
    private final FeatureRepository featureRepository;

    /// 更新
    @Transactional
    public void update(Feature entity) {
        var dbEntity = obtainById(entity.getId());
        beanMapper.map(dbEntity, entity);
        dbEntity.setEffectiveFrom(entity.getEffectiveFrom())
                .setEffectiveTo(entity.getEffectiveTo())
                .setStartTime(entity.getStartTime())
                .setEndTime(entity.getEndTime())
                .setWeekdays(entity.getWeekdays())
                .setAllowUserRb(entity.getAllowUserRb())
                .setDenyUserRb(entity.getDenyUserRb());
        featureRepository.update(dbEntity);
    }

    /// 查询指定功能
    @Transactional(readOnly = true)
    public Feature findById(int featureId) {
        return obtainById(featureId);
    }

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Feature> findPage(DataFilter dataFilter) {
        return featureRepository.findPage(dataFilter);
    }

    private Feature obtainById(int featureId) {
        var b = featureRepository.findById(featureId);
        if (b == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到功能: %s", featureId);
        }
        return b;
    }
}
