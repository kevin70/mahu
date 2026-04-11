package cool.houge.mahu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.helidon.common.parameters.Parameters;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PageTest {

    @Test
    void of_json_nullable_values_回退到默认分页参数() {
        var page = Page.of(null, null, null, null);

        assertEquals(1, page.getPage());
        assertEquals(20, page.getPageSize());
        assertTrue(page.isIncludeTotal());
        assertSame(Sort.unsorted(), page.getSort());
    }

    @Test
    void of_parameters_读取分页和排序参数() {
        var params = Parameters.create(
                "query",
                Map.of(
                        "page", List.of("2"),
                        "page_size", List.of("50"),
                        "include_total", List.of("false"),
                        "sort", List.of("-createdAt", "name")));

        var page = Page.of(params);

        assertEquals(2, page.getPage());
        assertEquals(50, page.getPageSize());
        assertFalse(page.isIncludeTotal());
        assertEquals(50L, page.getOffset());
        assertEquals(2, page.sortOrders().size());
        assertEquals("createdAt", page.sortOrders().getFirst().getProperty());
        assertTrue(page.sortOrders().getFirst().isDesc());
    }

    @Test
    void builder_默认值可直接构建() {
        var page = Page.builder().build();

        assertEquals(1, page.getPage());
        assertEquals(20, page.getPageSize());
        assertTrue(page.isIncludeTotal());
        assertTrue(page.getSort().isUnsorted());
    }

    @Test
    void of_非法页码抛出异常() {
        assertThrows(IllegalArgumentException.class, () -> Page.of(0, 20, true, Sort.unsorted()));
    }
}
