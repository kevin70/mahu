package cool.houge.mahu.shared.service;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictType;
import cool.houge.mahu.shared.ImmutableFeature;
import cool.houge.mahu.shared.repository.sys.DelayedTaskRepository;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 基础服务类
///
/// 该类提供了一些基础的服务方法，包括获取功能、字典项和字典类型，以及推送延迟消息。
/// 这些方法可以被其他服务类继承或直接使用。
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class SharedBaseService {

    /// 功能助手
    private final FeatureHelper featureHelper;
    /// 字典助手
    private final DicHelper dicHelper;
    /// 延迟消息仓库
    private final DelayedTaskRepository delayedTaskRepository;

    /// 获取指定的功能
    ///
    /// 通过功能ID加载并返回对应的功能对象。
    ///
    /// @param featureId 功能ID
    /// @return 对应的功能对象
    public @NonNull ImmutableFeature getFeature(int featureId) {
        return featureHelper.loadFeature(featureId);
    }

    /// 获取指定的字典项
    ///
    /// 通过字典项ID加载并返回对应的字典项对象。
    ///
    /// @param dictId 字典项ID
    /// @return 对应的字典项对象
    public @NonNull ImmutableDict getDict(int dictId) {
        return dicHelper.loadDict(dictId);
    }

    /// 获取指定的字典类型
    ///
    /// 通过字典类型ID加载并返回对应的字典类型对象。
    ///
    /// @param typeId 字典类型ID
    /// @return 对应的字典类型对象
    public @NonNull ImmutableDictType getDictType(String typeId) {
        return dicHelper.loadDictType(typeId);
    }

    /// 查询所有公共的字典类型
    public List<ImmutableDictType> loadPublicDictTypes() {
        return dicHelper.allDictTypes().stream()
                .filter(ImmutableDictType::isPublic)
                .toList();
    }

    /// 推送延时任务
    ///
    /// 将延时任务保存到数据库中，并进行批量保存操作。
    ///
    /// @param task 延时任务
    @Transactional
    public void emit(DelayedTask task) {
        delayedTaskRepository.batchSave(task);
    }
}
