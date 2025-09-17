package cool.houge.blma.shared.service;

import cool.houge.blma.shared.repository.SharedDictRepository;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.entity.Dict;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service.Singleton;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 字典公共服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
@AllArgsConstructor
public class SharedDictService {

    final SharedDictRepository sharedDictRepository;

    /// 加载指定字典数据
    @Transactional(readOnly = true)
    public @NonNull Dict loadByCode(String code) {
        var o = sharedDictRepository.findById(code);
        if (o == null) {
            throw new BizCodeException(BizCodes.DATA_LOSS, "未找到[" + code + "]字典数据");
        }
        return o;
    }
}
