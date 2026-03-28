package cool.houge.mahu.shared.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.benmanes.caffeine.cache.Cache;
import cool.houge.mahu.BizCodeException;
import cool.houge.mahu.BizCodes;
import cool.houge.mahu.entity.Dict;
import cool.houge.mahu.entity.DictGroup;
import cool.houge.mahu.repository.DictGroupRepository;
import cool.houge.mahu.repository.DictRepository;
import cool.houge.mahu.shared.ImmutableDict;
import cool.houge.mahu.shared.ImmutableDictGroup;
import io.helidon.service.registry.Services;
import io.helidon.testing.junit5.Testing;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/// {@link DictCacheService} 单元测试。
///
/// 通过 {@link Services#set} 在每个测试方法的 Registry 中注入 {@link DictGroupRepository} 和
/// {@link DictRepository} mock，再由 Helidon DI 将 {@link DictCacheService} 作为方法参数注入。
/// Config 依赖由 Registry 自动提供（测试环境无配置文件时降级为空节点，FixedRate 使用默认值）。
@Testing.Test(perMethod = true)
class DictCacheServiceTest {

    private final DictGroupRepository dictGroupRepository = mock(DictGroupRepository.class);
    private final DictRepository dictRepository = mock(DictRepository.class);

    /// 在 DictCacheService 被首次请求前，将 DictGroupRepository 和 DictRepository mock 注入当前方法的 Registry。
    @BeforeEach
    void setUp() {
        Services.set(DictGroupRepository.class, dictGroupRepository);
        Services.set(DictRepository.class, dictRepository);
    }

    @Test
    void loadDictType_returns_cached_value(DictCacheService service) throws Exception {
        var cached = ImmutableDictGroup.builder()
                .id("group-public")
                .name("公共字典")
                .description("d")
                .enabled(true)
                .visibility(1)
                .preset(false)
                .dicts(List.of())
                .build();
        dictTypeCache(service).put("group-public", cached);

        var loaded = service.loadDictType("group-public");

        assertSame(cached, loaded);
    }

    @Test
    void loadDictType_on_cache_miss_loads_repository_and_sorts_dicts_desc(DictCacheService service) {
        var group = new DictGroup()
                .setId("group-public")
                .setName("公共字典")
                .setDescription("d")
                .setEnabled(true)
                .setVisibility(DictGroup.Visibility.PUBLIC)
                .setPreset(false);

        var low = new Dict()
                .setDc(-101)
                .setGroup(group)
                .setLabel("low")
                .setValue("LOW")
                .setEnabled(true)
                .setOrdering(1)
                .setColor("#111111")
                .setPreset(false);
        var high = new Dict()
                .setDc(-202)
                .setGroup(group)
                .setLabel("high")
                .setValue("HIGH")
                .setEnabled(true)
                .setOrdering(9)
                .setColor("#999999")
                .setPreset(false);
        group.setData(List.of(low, high));

        when(dictGroupRepository.findById("group-public")).thenReturn(group);

        var loaded = service.loadDictType("group-public");
        var loadedAgain = service.loadDictType("group-public");

        assertEquals("group-public", loaded.getId());
        assertEquals(List.of(-202, -101), loaded.getDicts().stream().map(ImmutableDict::getDc).toList());
        assertEquals(loaded, loadedAgain);
        verify(dictGroupRepository, times(1)).findById("group-public");
    }

    @Test
    void loadDict_throws_data_loss_when_repository_returns_null(DictCacheService service) {
        when(dictRepository.findById(-999)).thenReturn(null);

        var ex = assertThrows(BizCodeException.class, () -> service.loadDict(-999));

        assertEquals(BizCodes.DATA_LOSS, ex.getCode());
    }

    @Test
    void allDictTypes_returns_cached_values(DictCacheService service) throws Exception {
        dictTypeCache(service)
                .put(
                        "g1",
                        ImmutableDictGroup.builder()
                                .id("g1")
                                .name("g1")
                                .description("d")
                                .enabled(true)
                                .visibility(1)
                                .preset(false)
                                .dicts(List.of())
                                .build());
        dictTypeCache(service)
                .put(
                        "g2",
                        ImmutableDictGroup.builder()
                                .id("g2")
                                .name("g2")
                                .description("d")
                                .enabled(true)
                                .visibility(0)
                                .preset(false)
                                .dicts(List.of())
                                .build());

        var all = service.allDictTypes();

        assertEquals(2, all.size());
    }

    /// 通过反射获取 DictCacheService 内部的 Caffeine 缓存实例，用于测试中预填缓存数据。
    @SuppressWarnings("unchecked")
    private static Cache<String, ImmutableDictGroup> dictTypeCache(DictCacheService service) throws Exception {
        Field field = DictCacheService.class.getDeclaredField("dictTypeCache");
        field.setAccessible(true);
        return (Cache<String, ImmutableDictGroup>) field.get(service);
    }
}
