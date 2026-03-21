package cool.houge.mahu.shared.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.Status;
import cool.houge.mahu.delayed.DelayedTaskTopics;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
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

    /// 确保指定的功能开关处于生效状态，否则抛出业务异常。
    public @NonNull ImmutableFeatureFlag ensureFeatureFlagOn(String code) {
        ImmutableFeatureFlag flag;
        try {
            flag = getFeatureFlag(code);
        } catch (BizCodeException e) {
            throw e;
        } catch (Exception e) {
            throw new BizCodeException(BizCodes.INTERNAL, "加载功能开关失败: " + code, e);
        }
        if (!flag.isActive()) {
            throw new BizCodeException(BizCodes.PERMISSION_DENIED, "功能未开启: " + code);
        }
        return flag;
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
    public void enqueueDelayedTask(DelayedTask task) {
        delayedTaskRepository.enqueueDelayedTask(task);
    }

    /// 推送功能开关相关延时任务（payload 与 idempotencyKey 由外部提供）
    @Transactional
    public void enqueueDelayedTask(
            DelayedTaskTopics topic,
            @NonNull String referenceId,
            Instant expectedAt,
            String payload,
            String idempotencyKey) {
        var task = new DelayedTask();
        task.setFeatureId(topic.featureFlagId());
        task.setTopic(topic.topic());
        task.setReferenceId(referenceId);
        task.setStatus(Status.PENDING.getCode());
        task.setDelayUntil(expectedAt);
        task.setAttempts(0);
        task.setMaxAttempts(topic.maxAttempts());
        task.setLeaseSeconds(topic.leaseSeconds());
        task.setPayload(payload);
        task.setIdempotencyKey(idempotencyKey);
        delayedTaskRepository.enqueueDelayedTask(task);
    }

    /// 完成 delayed_task：置为 COMPLETED 并清空调度相关字段。
    @Transactional
    public void completeDelayedTask(UUID delayedTaskId) {
        delayedTaskRepository.complete(delayedTaskId);
    }

    /// 归档 delayed_task：置为 ARCHIVED 并清空调度相关字段。
    @Transactional
    public void archiveDelayedTask(UUID delayedTaskId) {
        delayedTaskRepository.archive(delayedTaskId);
    }
}
