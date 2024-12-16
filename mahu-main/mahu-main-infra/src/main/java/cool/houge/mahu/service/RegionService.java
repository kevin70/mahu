package cool.houge.mahu.service;

import cool.houge.mahu.common.BizCodeException;
import cool.houge.mahu.common.BizCodes;
import cool.houge.mahu.entity.Region;
import cool.houge.mahu.repository.RegionRepository;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

/// 行政区
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class RegionService {

    @Inject
    RegionRepository regionRepository;

    /// 查询指定深度以下（包含）的行政区
    @Transactional(readOnly = true)
    public List<Region> findDepthBound(int depth) {
        return regionRepository.findDepthBound(depth);
    }

    /// 获取指定代码的行政区及子行政区
    @Transactional(readOnly = true)
    public Region loadRegionFully(String code) {
        var region = obtainByCode(code);
        this.loadChildren(region);
        return region;
    }

    /// 获取指定代码的行政区
    @Transactional(readOnly = true)
    public Region obtainByCode(String code) {
        var region = regionRepository.findById(code);
        if (region == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND);
        }
        return region;
    }

    private void loadChildren(Region region) {
        if (region.getDepth() < 4) {
            region.setChildren(regionRepository.findByParentCode(region.getCode()));
        }
    }
}
