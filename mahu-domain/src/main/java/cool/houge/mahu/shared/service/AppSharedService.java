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
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 应用级共享服务。
///
/// 聚合系统内跨模块常用的基础能力，供业务模块以统一入口访问：
/// - 功能开关读取与可用性校验
/// - 字典与字典分组读取
/// - 延时任务入队
///
/// 该类只负责服务编排与参数组装，具体缓存/持久化细节由下游服务与仓储实现。
@Service.Singleton
@AllArgsConstructor
public class AppSharedService {

    /// 功能开关缓存服务，负责读取与缓存功能开关配置。
    private final FeatureFlagCacheService featureFlagCacheService;
    /// 字典缓存服务，负责读取与缓存字典及字典分组。
    private final DictCacheService dictCacheService;
    /// 延时任务仓储，负责延时任务持久化入队。
    private final DelayedTaskRepository delayedTaskRepository;

    /// 按编码获取功能开关配置。
    ///
    /// @param code 功能开关编码
    /// @return 功能开关快照（不存在时由下游抛出业务异常）
    public @NonNull ImmutableFeatureFlag getFeatureFlag(String code) {
        return featureFlagCacheService.loadFeature(code);
    }

    /// 确保指定功能开关已开启，否则抛出业务异常。
    ///
    /// 语义约束：
    /// - 找不到功能开关或读取失败时，向上抛出业务异常
    /// - 功能开关存在但未开启时，抛出 `PERMISSION_DENIED`
    ///
    /// @param code 功能开关编码
    /// @return 已开启的功能开关快照
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

    /// 按字典 ID 获取字典项。
    ///
    /// @param dictId 字典 ID
    /// @return 字典项快照
    public @NonNull ImmutableDict getDict(int dictId) {
        return dictCacheService.loadDict(dictId);
    }

    /// 按分组 ID 获取字典分组。
    ///
    /// @param groupId 字典分组 ID
    /// @return 字典分组快照
    public @NonNull ImmutableDictGroup getDictGroup(String groupId) {
        return dictCacheService.loadDictType(groupId);
    }

    /// 查询所有标记为公开的字典分组。
    ///
    /// @return 公开字典分组列表
    public List<ImmutableDictGroup> loadPublicDictGroups() {
        return dictCacheService.allDictTypes().stream()
                .filter(ImmutableDictGroup::isPublic)
                .toList();
    }

    /// 直接入队一个已构造完成的延时任务实体。
    ///
    /// 适用于调用方已自行填充任务字段的场景。
    ///
    /// @param task 延时任务实体
    @Transactional
    public void enqueueDelayedTask(DelayedTask task) {
        delayedTaskRepository.enqueueDelayedTask(task);
    }

    /// 根据主题与基础参数构造并入队延时任务（不携带 payload）。
    ///
    /// @param topic 延时任务主题定义（包含默认重试与租约配置）
    /// @param referenceId 业务引用 ID，用于关联业务对象
    /// @param expectedAt 期望执行时间（延时到期时间）
    /// @param idempotencyKey 幂等键，可为空
    public void enqueueDelayedTask(
            DelayedTaskTopics topic, @NonNull String referenceId, Instant expectedAt, String idempotencyKey) {
        this.enqueueDelayedTask(topic, referenceId, expectedAt, idempotencyKey, null);
    }

    /// 根据主题定义构造并入队延时任务。
    ///
    /// 该方法会自动填充以下字段：
    /// - `featureId/topic/maxAttempts/leaseSeconds`：来自 `DelayedTaskTopics`
    /// - `status/attempts`：初始化为待处理状态与 0 次尝试
    ///
    /// @param topic 延时任务主题定义
    /// @param referenceId 业务引用 ID
    /// @param expectedAt 期望执行时间（延时到期时间）
    /// @param idempotencyKey 幂等键，可为空
    /// @param payload 任务负载，可为空
    @Transactional
    public void enqueueDelayedTask(
            DelayedTaskTopics topic,
            @NonNull String referenceId,
            Instant expectedAt,
            String idempotencyKey,
            String payload) {
        var task = new DelayedTask();
        task.setFeatureId(topic.featureFlagId());
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
