package cool.houge.mahu.system.service;

import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.entity.system.Dict;
import cool.houge.mahu.system.repository.DictRepository;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

/// 字典
///
/// @author ZY (kzou227@qq.com
@Singleton
public class DictService {

    @Inject
    DictRepository dictRepository;

    /// 查询指定字典类型的数据
    @Transactional(readOnly = true)
    public List<Dict> findByKind(String kind) {
        return dictRepository.findByKind(kind);
    }

    /// 获取指定 SLUG 的字典数据
    @Transactional(readOnly = true)
    public Dict obtainBySlug(String slug) {
        var bean = dictRepository.findBySlug(slug);
        if (bean == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到指定 SLUG 的字典");
        }
        return bean;
    }
}
