package cool.houge.mahu.shared.service;

import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 应用级共享服务。
///
/// 聚合系统内跨模块常用的基础服务能力，例如：
/// - 读取功能配置与功能开关
/// - 读取字典项与字典分组
/// - 推送延时任务
@Service.Singleton
@AllArgsConstructor
public class AppSharedService {

    /// 功能开关缓存服务
    private final FeatureFlagCacheService featureFlagCacheService;
    /// 字典缓存服务
    private final DictCacheService dictCacheService;
    /// 延迟消息仓库
    private final DelayedTaskRepository delayedTaskRepository;

    /// 获取指定的功能开关
    public @NonNull ImmutableFeatureFlag getFeatureFlag(String code) {
        return featureFlagCacheService.loadFeature(code);
    }

    /// 获取指定的字典项
    public @NonNull ImmutableDict getDict(int dictId) {
        return dictCacheService.loadDict(dictId);
    }

    /// 获取指定的字典分组
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
    @Transactional
    public void emit(DelayedTask task) {
        delayedTaskRepository.batchSave(task);
    }
}

