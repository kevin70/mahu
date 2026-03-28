package cool.houge.mahu.shared.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.config.DelayedTaskTopic;
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

@Service.Singleton
@AllArgsConstructor
public class AppSharedService {
    private final FeatureFlagCacheService featureFlagCacheService;
    private final DictCacheService dictCacheService;
    private final DelayedTaskRepository delayedTaskRepository;

    public @NonNull ImmutableFeatureFlag getFeatureFlag(String code) {
        return featureFlagCacheService.loadFeature(code);
    }

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

    public @NonNull ImmutableDict getDict(int dictId) {
        return dictCacheService.loadDict(dictId);
    }

    public @NonNull ImmutableDictGroup getDictGroup(String groupId) {
        return dictCacheService.loadDictType(groupId);
    }

    public List<ImmutableDictGroup> loadPublicDictGroups() {
        return dictCacheService.allDictTypes().stream()
                .filter(ImmutableDictGroup::isPublic)
                .toList();
    }

    @Transactional
    public void enqueueDelayedTask(DelayedTask task) {
        delayedTaskRepository.enqueueDelayedTask(task);
    }

    public void enqueueDelayedTask(
            DelayedTaskTopic topic, @NonNull String referenceId, Instant expectedAt, String idempotencyKey) {
        this.enqueueDelayedTask(topic, referenceId, expectedAt, idempotencyKey, null);
    }

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
