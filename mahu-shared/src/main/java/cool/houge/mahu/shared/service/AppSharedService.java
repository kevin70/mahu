package cool.houge.mahu.shared.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.DelayedTaskTopic;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// `mahu-shared` 对外暴露的共享业务门面。
///
/// 统一封装功能开关、字典缓存与延迟任务入队等跨模块都会用到的能力，
/// 供管理端、任务模块等上层应用通过 DI 直接复用。
@Service.Singleton
@AllArgsConstructor
public class AppSharedService {
    private final FeatureFlagCacheService featureFlagCacheService;
    private final DictCacheService dictCacheService;
    private final DelayedTaskRepository delayedTaskRepository;

    /// 读取指定功能开关的当前缓存快照。
    public @NonNull ImmutableFeatureFlag getFeatureFlag(String code) {
        return featureFlagCacheService.loadFeature(code);
    }

    /// 校验功能开关已开启。
    ///
    /// 对业务层暴露统一的异常语义：缓存/仓储层抛出的业务异常原样透传，
    /// 其他异常统一包装为内部错误，便于上层接口返回稳定的错误码。
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

    /// 按字典编码读取单个字典项。
    public @NonNull ImmutableDict getDict(int dictId) {
        return dictCacheService.loadDict(dictId);
    }

    /// 按分组 ID 读取整组字典。
    public @NonNull ImmutableDictGroup getDictGroup(String groupId) {
        return dictCacheService.loadDictType(groupId);
    }

    /// 返回当前对外公开的字典分组。
    ///
    /// 该方法基于缓存快照做过滤，不会再次访问数据库。
    public List<ImmutableDictGroup> loadPublicDictGroups() {
        return dictCacheService.allDictTypes().stream()
                .filter(ImmutableDictGroup::isPublic)
                .toList();
    }

    /// 直接将已构造好的延迟任务实体写入仓储层。
    @Transactional
    public void enqueueDelayedTask(DelayedTask task) {
        delayedTaskRepository.enqueueDelayedTask(task);
    }

    /// 使用默认空负载创建延迟任务。
    public void enqueueDelayedTask(
            DelayedTaskTopic topic, @NonNull String referenceId, Instant expectedAt, String idempotencyKey) {
        this.enqueueDelayedTask(topic, referenceId, expectedAt, idempotencyKey, null);
    }

    /// 根据主题约定补齐默认状态、重试次数与租约等字段后入队。
    ///
    /// 这里统一使用 `Status.PENDING` 作为新任务初始状态，避免调用方自行拼装时遗漏关键字段。
    @Transactional
    public void enqueueDelayedTask(
            DelayedTaskTopic topic,
            @NonNull String referenceId,
            Instant expectedAt,
            String idempotencyKey,
            String payload) {
        var task = new DelayedTask();
        task.setTopic(topic.topic());
        task.setReferenceId(referenceId);
        task.setStatus(Status.PENDING.getCode());
        task.setDelayUntil(expectedAt);
        task.setAttempts(0);
        task.setMaxAttempts(topic.maxAttempts());
        task.setLeaseSeconds(topic.leaseSeconds());
        task.setIdempotencyKey(idempotencyKey);
        if (payload != null) {
            task.setPayload(payload);
        }
        delayedTaskRepository.enqueueDelayedTask(task);
    }
}
