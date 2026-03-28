package cool.houge.mahu.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.DelayedTaskTopic;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/// {@link AppSharedService} 单元测试。
///
/// 通过 {@link Services#set} 在每个测试方法的 Registry 中注入 {@link FeatureFlagCacheService}、
/// {@link DictCacheService} 和 {@link DelayedTaskRepository} mock，
/// 再由 Helidon DI 将 {@link AppSharedService} 作为方法参数注入。
@Testing.Test(perMethod = true)
class AppSharedServiceTest {

    private final FeatureFlagCacheService featureFlagCacheService = mock(FeatureFlagCacheService.class);
    private final DictCacheService dictCacheService = mock(DictCacheService.class);
    private final DelayedTaskRepository delayedTaskRepository = mock(DelayedTaskRepository.class);

    /// 在 AppSharedService 被首次请求前，将三个 mock 实例注入当前方法的 Registry。
    /// Services.set() 必须在 contract 第一次被使用之前调用。
    @BeforeEach
    void setUp() {
        Services.set(FeatureFlagCacheService.class, featureFlagCacheService);
        Services.set(DictCacheService.class, dictCacheService);
        Services.set(DelayedTaskRepository.class, delayedTaskRepository);
    }

    @Test
    void ensureFeatureFlagOn_returns_flag_when_active(AppSharedService service) {
        var flag = ImmutableFeatureFlag.builder()
                .id(-101)
                .code("pay.wechat")
                .name("微信支付")
                .description("d")
                .enabled(true)
                .preset(false)
                .ordering(1)
                .build();
        when(featureFlagCacheService.loadFeature("pay.wechat")).thenReturn(flag);

        var loaded = service.ensureFeatureFlagOn("pay.wechat");

        assertSame(flag, loaded);
    }

    @Test
    void ensureFeatureFlagOn_throws_permission_denied_when_inactive(AppSharedService service) {
        var flag = ImmutableFeatureFlag.builder()
                .id(-101)
                .code("pay.wechat")
                .name("微信支付")
                .description("d")
                .enabled(false)
                .preset(false)
                .ordering(1)
                .build();
        when(featureFlagCacheService.loadFeature("pay.wechat")).thenReturn(flag);

        var ex = assertThrows(BizCodeException.class, () -> service.ensureFeatureFlagOn("pay.wechat"));

        assertEquals(BizCodes.PERMISSION_DENIED, ex.getCode());
    }

    @Test
    void ensureFeatureFlagOn_wraps_unknown_error_as_internal(AppSharedService service) {
        when(featureFlagCacheService.loadFeature("pay.wechat")).thenThrow(new IllegalStateException("boom"));

        var ex = assertThrows(BizCodeException.class, () -> service.ensureFeatureFlagOn("pay.wechat"));

        assertEquals(BizCodes.INTERNAL, ex.getCode());
        org.junit.jupiter.api.Assertions.assertTrue(ex.getRawMessage().startsWith("加载功能开关失败: pay.wechat"));
    }

    @Test
    void loadPublicDictGroups_filters_public_groups_only(AppSharedService service) {
        var publicGroup = ImmutableDictGroup.builder()
                .id("public")
                .name("公共")
                .description("d")
                .enabled(true)
                .visibility(1)
                .preset(false)
                .dicts(List.of())
                .build();
        var privateGroup = ImmutableDictGroup.builder()
                .id("private")
                .name("私有")
                .description("d")
                .enabled(true)
                .visibility(0)
                .preset(false)
                .dicts(List.of())
                .build();
        when(dictCacheService.allDictTypes()).thenReturn(List.of(publicGroup, privateGroup));

        var list = service.loadPublicDictGroups();

        assertEquals(1, list.size());
        assertEquals("public", list.getFirst().getId());
    }

    @Test
    void enqueueDelayedTask_builds_task_fields_from_topic_and_input(AppSharedService service) {
        var expectedAt = Instant.parse("2026-03-28T12:00:00Z");

        service.enqueueDelayedTask(
                DelayedTaskTopic.FEATURE_FLAG_ENABLE,
                "ref-feature-1",
                expectedAt,
                "idem-1",
                "{\"k\":\"v\"}");

        var taskCaptor = ArgumentCaptor.forClass(DelayedTask.class);
        verify(delayedTaskRepository).enqueueDelayedTask(taskCaptor.capture());
        var task = taskCaptor.getValue();

        assertNotNull(task);
        assertEquals(DelayedTaskTopic.FEATURE_FLAG_ENABLE.topic(), task.getTopic());
        assertEquals("ref-feature-1", task.getReferenceId());
        assertEquals(Status.PENDING.getCode(), task.getStatus());
        assertEquals(expectedAt, task.getDelayUntil());
        assertEquals(0, task.getAttempts());
        assertEquals(DelayedTaskTopic.FEATURE_FLAG_ENABLE.maxAttempts(), task.getMaxAttempts());
        assertEquals(DelayedTaskTopic.FEATURE_FLAG_ENABLE.leaseSeconds(), task.getLeaseSeconds());
        assertEquals("idem-1", task.getIdempotencyKey());
        assertEquals("{\"k\":\"v\"}", task.getPayload());
    }

    @Test
    void enqueueDelayedTask_with_entity_delegates_to_repository(AppSharedService service) {
        var task = new DelayedTask().setReferenceId("ref-plain");

        service.enqueueDelayedTask(task);

        verify(delayedTaskRepository).enqueueDelayedTask(any(DelayedTask.class));
    }
}
