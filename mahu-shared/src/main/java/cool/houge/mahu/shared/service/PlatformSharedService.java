package cool.houge.mahu.shared.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.model.command.EnqueueDelayedTaskCommand;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;

/// 平台级共享能力门面。
///
/// 聚合系统内高频复用的共享能力，包括功能开关、字典快照和延迟任务入队。
@Service.Singleton
@AllArgsConstructor
public class PlatformSharedService {
    private final ObjectMapper objectMapper;
    private final FeatureFlagCacheService featureFlagCacheService;
    private final DictCacheService dictCacheService;
    private final DelayedTaskRepository delayedTaskRepository;

    /// 读取指定功能开关的当前缓存快照。
    public @NonNull ImmutableFeatureFlag getFeatureFlag(String code) {
        return featureFlagCacheService.loadFeature(code);
    }

    /// 校验功能开关已开启，并返回当前快照。
    ///
    /// 业务异常原样透传，其它异常统一包装为 `BizCodes.INTERNAL`。
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

    /// 返回当前公开字典分组快照。
    ///
    /// 该方法只基于本地缓存过滤，不会再次访问数据库。
    public List<ImmutableDictGroup> loadPublicDictGroups() {
        return dictCacheService.allDictTypes().stream()
                .filter(ImmutableDictGroup::isPublic)
                .toList();
    }

    /// 按命令构造延迟任务并入队。
    public void enqueueDelayedTask(@NonNull EnqueueDelayedTaskCommand command) {
        enqueueDelayedTask(buildDelayedTask(command));
    }

    /// 直接写入已构造好的延迟任务实体。
    @Transactional
    void enqueueDelayedTask(DelayedTask task) {
        delayedTaskRepository.enqueueDelayedTask(task);
    }

    /// 根据主题约定补齐默认字段，构造待入队的延迟任务。
    ///
    /// 新任务统一使用 `Status.PENDING` 作为初始状态。
    private DelayedTask buildDelayedTask(@NonNull EnqueueDelayedTaskCommand command) {
        var topic = Objects.requireNonNull(command.getTopic(), "topic");
        var referenceId = Objects.requireNonNull(command.getReferenceId(), "referenceId");
        var expectedAt = Objects.requireNonNull(command.getExpectedAt(), "expectedAt");
        var idempotencyKey = Objects.requireNonNull(command.getIdempotencyKey(), "idempotencyKey");
        var task = new DelayedTask();
        task.setTopic(topic.topic());
        task.setReferenceId(referenceId);
        task.setStatus(Status.PENDING.getCode());
        task.setDelayUntil(expectedAt);
        task.setAttempts(0);
        task.setMaxAttempts(topic.maxAttempts());
        task.setLeaseSeconds(topic.leaseSeconds());
        task.setIdempotencyKey(idempotencyKey);
        var payload = command.getPayload();
        if (payload != null) {
            task.setPayload(serializePayload(payload));
        }
        return task;
    }

    /// 将任务负载转换为可落库的 JSON 字符串。
    ///
    /// 字符串输入按现有约定原样透传，其它对象统一序列化为 JSON。
    private String serializePayload(Object payload) {
        if (payload instanceof String json) {
            return json;
        }
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new BizCodeException(BizCodes.INTERNAL, "序列化延迟任务 payload 失败", e);
        }
    }
}
