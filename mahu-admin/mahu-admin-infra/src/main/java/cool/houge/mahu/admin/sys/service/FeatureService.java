package cool.houge.mahu.admin.sys.service;

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

    private final FeatureRepository featureRepository;

    /// 分页查询
    @Transactional(readOnly = true)
    public PagedList<Feature> findPage(DataFilter dataFilter) {
        return featureRepository.findPage(dataFilter);
    }
}
