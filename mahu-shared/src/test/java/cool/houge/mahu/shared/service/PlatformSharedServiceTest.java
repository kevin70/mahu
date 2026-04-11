package cool.houge.mahu.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.config.DelayedTaskTopic;
import cool.houge.mahu.config.Status;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.model.command.EnqueueDelayedTaskCommand;
import cool.houge.mahu.repository.sys.DelayedTaskRepository;
import cool.houge.mahu.shared.ImmutableDictGroup;
import cool.houge.mahu.shared.ImmutableFeatureFlag;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/// {@link PlatformSharedService} 单元测试。
///
/// 通过 {@link Services#set} 在每个测试方法的 Registry 中注入 {@link FeatureFlagCacheService}、
/// {@link DictCacheService} 和 {@link DelayedTaskRepository} mock，
/// 再由 Helidon DI 将 {@link PlatformSharedService} 作为方法参数注入。
@Testing.Test(perMethod = true)
class PlatformSharedServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    private final FeatureFlagCacheService featureFlagCacheService = mock(FeatureFlagCacheService.class);
    private final DictCacheService dictCacheService = mock(DictCacheService.class);
    private final DelayedTaskRepository delayedTaskRepository = mock(DelayedTaskRepository.class);

    /// 在 PlatformSharedService 被首次请求前，将三个 mock 实例注入当前方法的 Registry。
    /// Services.set() 必须在 contract 第一次被使用之前调用。
    @BeforeEach
    void setUp() {
        Services.set(ObjectMapper.class, objectMapper);
        Services.set(FeatureFlagCacheService.class, featureFlagCacheService);
        Services.set(DictCacheService.class, dictCacheService);
        Services.set(DelayedTaskRepository.class, delayedTaskRepository);
    }

    @Test
    void ensureFeatureFlagOn_returns_flag_when_active(PlatformSharedService service) {
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
    void ensureFeatureFlagOn_throws_permission_denied_when_inactive(PlatformSharedService service) {
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
    void ensureFeatureFlagOn_wraps_unknown_error_as_internal(PlatformSharedService service) {
        when(featureFlagCacheService.loadFeature("pay.wechat")).thenThrow(new IllegalStateException("boom"));

        var ex = assertThrows(BizCodeException.class, () -> service.ensureFeatureFlagOn("pay.wechat"));

        assertEquals(BizCodes.INTERNAL, ex.getCode());
        org.junit.jupiter.api.Assertions.assertTrue(ex.getRawMessage().startsWith("加载功能开关失败: pay.wechat"));
    }

    @Test
    void loadPublicDictGroups_filters_public_groups_only(PlatformSharedService service) {
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
    void enqueueTopicDelayedTask_uses_null_payload_when_not_provided(PlatformSharedService service) {
        var expectedAt = Instant.parse("2026-03-28T12:00:00Z");

        service.enqueueTopicDelayedTask(EnqueueDelayedTaskCommand.builder()
                .topic(DelayedTaskTopic.FEATURE_FLAG_ENABLE)
                .referenceId("ref-feature-1")
                .expectedAt(expectedAt)
                .idempotencyKey("idem-1")
                .build());

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
        assertNull(task.getPayload());
    }

    @Test
    void enqueueTopicDelayedTask_builds_task_fields_from_command_with_payload(PlatformSharedService service) {
        var expectedAt = Instant.parse("2026-03-28T12:00:00Z");

        service.enqueueTopicDelayedTask(EnqueueDelayedTaskCommand.builder()
                .topic(DelayedTaskTopic.FEATURE_FLAG_ENABLE)
                .referenceId("ref-feature-1")
                .expectedAt(expectedAt)
                .idempotencyKey("idem-1")
                .payload("{\"k\":\"v\"}")
                .build());

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
    void enqueueTopicDelayedTask_serializes_object_payload_as_json(PlatformSharedService service) {
        var expectedAt = Instant.parse("2026-03-28T12:00:00Z");

        service.enqueueTopicDelayedTask(EnqueueDelayedTaskCommand.builder()
                .topic(DelayedTaskTopic.FEATURE_FLAG_ENABLE)
                .referenceId("ref-feature-2")
                .expectedAt(expectedAt)
                .idempotencyKey("idem-2")
                .payload(Map.of("k", "v"))
                .build());

        var taskCaptor = ArgumentCaptor.forClass(DelayedTask.class);
        verify(delayedTaskRepository).enqueueDelayedTask(taskCaptor.capture());
        var task = taskCaptor.getValue();

        assertNotNull(task);
        assertEquals("{\"k\":\"v\"}", task.getPayload());
    }

    @Test
    void enqueueDelayedTask_with_entity_delegates_to_repository(PlatformSharedService service) {
        var task = new DelayedTask().setReferenceId("ref-plain");

        service.enqueueDelayedTask(task);

        verify(delayedTaskRepository).enqueueDelayedTask(any(DelayedTask.class));
    }
}
