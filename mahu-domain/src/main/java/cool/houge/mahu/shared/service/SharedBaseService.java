package cool.houge.mahu.shared.service;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeature;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 基础服务类
///
/// 该类提供了一些基础的服务方法，包括获取功能、字典项和字典分组，以及推送延迟消息。
/// 这些方法可以被其他服务类继承或直接使用。
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
@AllArgsConstructor
public class SharedBaseService {

    /// 功能配置缓存助手（旧版）
    private final FeatureHelper featureHelper;
    /// 功能开关缓存服务
    private final FeatureFlagCacheService featureFlagCacheService;
    /// 字典缓存服务
    private final DictCacheService dictCacheService;
    /// 延迟消息仓库
    private final DelayedTaskRepository delayedTaskRepository;

    /// 获取指定的功能（基于旧版功能配置表）
    ///
    /// 通过功能ID加载并返回对应的功能对象。
    ///
    /// @param featureId 功能ID
    /// @return 对应的功能对象
    public @NonNull ImmutableFeature getFeature(int featureId) {
        return featureHelper.loadFeature(featureId);
    }

    /// 获取指定的功能开关
    ///
    /// 通过功能开关 code 加载并返回对应的快照对象。
    ///
    /// @param code 功能开关 code（如 `payment.wechat`）
    /// @return 对应的功能开关快照
    public @NonNull ImmutableFeatureFlag getFeatureFlag(String code) {
        return featureFlagCacheService.loadFeature(code);
    }

    /// 获取指定的字典项
    ///
    /// 通过字典项ID加载并返回对应的字典项对象。
    ///
    /// @param dictId 字典项ID
    /// @return 对应的字典项对象
    public @NonNull ImmutableDict getDict(int dictId) {
        return dictCacheService.loadDict(dictId);
    }

    /// 获取指定的字典分组
    ///
    /// 通过字典分组ID加载并返回对应的字典分组对象。
    ///
    /// @param groupId 字典分组ID
    /// @return 对应的字典分组对象
    public @NonNull ImmutableDictGroup getDictGroup(String groupId) {
        return dictCacheService.loadDictType(groupId);
    }

    /// 查询所有公共的字典分组
    public List<ImmutableDictGroup> loadPublicDictGroups() {
        return dictCacheService.allDictTypes().stream()
                .filter(ImmutableDictGroup::isPublic)
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
