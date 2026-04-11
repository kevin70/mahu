package cool.houge.mahu.admin.sys.service;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.admin.bean.EntityBeanMapper;
import cool.houge.mahu.config.DelayedTaskTopic;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.FeatureFlag;
import cool.houge.mahu.model.command.EnqueueDelayedTaskCommand;
import cool.houge.mahu.model.query.FeatureFlagQuery;
import cool.houge.mahu.repository.sys.FeatureFlagRepository;
import cool.houge.mahu.shared.service.PlatformSharedService;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import io.helidon.service.registry.Service;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;

/// 功能开关服务
@Service.Singleton
@AllArgsConstructor
public class FeatureFlagService {

    private final EntityBeanMapper beanMapper;
    private final FeatureFlagRepository featureFlagRepository;
    private final PlatformSharedService platformSharedService;

    /// 新建功能开关
    @Transactional
    public void create(FeatureFlag entity) {
        featureFlagRepository.save(entity);
        enqueueDelayedTasksForNewFlag(entity);
    }

    /// 更新功能开关
    @Transactional
    public void update(FeatureFlag entity) {
        var dbEntity = obtainById(entity.getId());
        var oldEnableAt = dbEntity.getEnableAt();
        var oldDisableAt = dbEntity.getDisableAt();
        // if (dbEntity.isPreset()) {
        //     entity.setCode(null);
        // }
        beanMapper.map(dbEntity, entity);
        featureFlagRepository.update(dbEntity);

        // 仅当 enableAt/disableAt 实际发生变化时，才会推送新的延迟任务；
        // 旧任务到点后在 worker 校验 expectedAt 不匹配时会被安全地 no-op 并归档。
        enqueueDelayedTasksIfNeeded(
                dbEntity.getId(), oldEnableAt, dbEntity.getEnableAt(), oldDisableAt, dbEntity.getDisableAt());
    }

    /// 查询指定功能开关
    @Transactional(readOnly = true)
    public FeatureFlag findById(int id) {
        return obtainById(id);
    }

    /// 分页查询功能开关
    @Transactional(readOnly = true)
    public PagedList<FeatureFlag> findPage(FeatureFlagQuery query, Page page) {
        return featureFlagRepository.findPage(query, page);
    }

    private FeatureFlag obtainById(int id) {
        var b = featureFlagRepository.findById(id);
        if (b == null) {
            throw new BizCodeException(BizCodes.NOT_FOUND, "未找到功能开关: %s", id);
        }
        return b;
    }

    private void enqueueDelayedTasksForNewFlag(FeatureFlag flag) {
        if (flag.getEnableAt() != null) {
            enqueueEnableDelayedTask(flag.getId(), flag.getEnableAt());
        }
        if (flag.getDisableAt() != null) {
            enqueueDisableDelayedTask(flag.getId(), flag.getDisableAt());
        }
    }

    private void enqueueDelayedTasksIfNeeded(
            int featureFlagId, Instant oldEnableAt, Instant newEnableAt, Instant oldDisableAt, Instant newDisableAt) {
        if (!Objects.equals(oldEnableAt, newEnableAt) && newEnableAt != null) {
            enqueueEnableDelayedTask(featureFlagId, newEnableAt);
        }
        if (!Objects.equals(oldDisableAt, newDisableAt) && newDisableAt != null) {
            enqueueDisableDelayedTask(featureFlagId, newDisableAt);
        }
    }

    private void enqueueEnableDelayedTask(int featureFlagId, Instant expectedEnableAt) {
        var topic = DelayedTaskTopic.FEATURE_FLAG_ENABLE;
        var idempotencyKey = idempotencyKey(featureFlagId, expectedEnableAt);
        platformSharedService.enqueueDelayedTask(EnqueueDelayedTaskCommand.builder()
                .topic(topic)
                .referenceId(String.valueOf(featureFlagId))
                .expectedAt(expectedEnableAt)
                .idempotencyKey(idempotencyKey)
                .build());
    }

    private void enqueueDisableDelayedTask(int featureFlagId, Instant expectedDisableAt) {
        var topic = DelayedTaskTopic.FEATURE_FLAG_DISABLE;
        var idempotencyKey = idempotencyKey(featureFlagId, expectedDisableAt);
        platformSharedService.enqueueDelayedTask(EnqueueDelayedTaskCommand.builder()
                .topic(topic)
                .referenceId(String.valueOf(featureFlagId))
                .expectedAt(expectedDisableAt)
                .idempotencyKey(idempotencyKey)
                .build());
    }

    private static String idempotencyKey(int featureFlagId, Instant expectedAt) {
        return featureFlagId + ":" + expectedAt;
    }
}
