package cool.houge.mahu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.helidon.common.parameters.Parameters;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SortTest {

    @Test
    void of_list_忽略空白并按属性去重保留首次顺序() {
        var sort = Sort.of(List.of("  name  ", "-createdAt", "-NAME", "-", " "));

        assertEquals(2, sort.getOrders().size());
        assertEquals("name", sort.getOrders().get(0).getProperty());
        assertTrue(sort.getOrders().get(0).isAsc());
        assertEquals("createdAt", sort.getOrders().get(1).getProperty());
        assertTrue(sort.getOrders().get(1).isDesc());
    }

    @Test
    void builder_可直接追加排序项() {
        var sort = Sort.builder().asc("name").desc("createdAt").build();

        assertEquals(2, sort.getOrders().size());
        assertEquals("name", sort.getOrders().getFirst().getProperty());
        assertTrue(sort.getOrders().getFirst().equalsIgnoreCase("NAME"));
        assertTrue(sort.getOrders().get(1).isDesc());
    }

    @Test
    void of_parameters_缺失排序参数返回空排序() {
        var sort = Sort.of(Parameters.createSingleValueMap("query", Map.of("page", "1")));

        assertTrue(sort.isUnsorted());
    }

    @Test
    void order_空白属性抛出异常() {
        assertThrows(IllegalArgumentException.class, () -> Sort.builder()
                .asc(" ")
                .build());
    }

    @Test
    void unsorted_不包含排序项() {
        assertFalse(Sort.unsorted().getOrders().iterator().hasNext());
    }
}
