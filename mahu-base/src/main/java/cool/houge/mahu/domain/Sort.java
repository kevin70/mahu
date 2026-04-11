/*
 * Copyright 2008-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cool.houge.mahu.domain;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.helidon.common.parameters.Parameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

/// 排序信息封装，提供流畅的 API 和不可变特性
///
/// @author ZY (kzou227@qq.com)
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Sort {

    /// 空排序
    private static final Sort UNSORTED = new Sort(List.of());

    /// 排序集合
    private final List<Order> orders;

    private Sort(List<Order> orders) {
        var normalizedOrders = normalizeOrders(orders);
        this.orders = normalizedOrders.isEmpty() ? List.of() : List.copyOf(normalizedOrders);
    }

    /// 空排序
    public static Sort unsorted() {
        return UNSORTED;
    }

    /// 根据传入的参数创建一个排序对象。
    ///
    /// 参数中可包含以下键值对：
    /// - `sort`：排序参数。
    ///
    /// @param params 包含排序参数的对象。
    /// @return 排序
    public static @NonNull Sort of(@NonNull Parameters params) {
        requireNonNull(params, "params");
        if (!params.contains("sort")) {
            return Sort.unsorted();
        }
        return of(params.all("sort"));
    }

    /// 将排序参数转换为 Sort 对象
    ///
    /// @param params 排序参数列表
    @JsonCreator
    public static @NonNull Sort of(List<String> params) {
        if (params == null || params.isEmpty()) {
            return Sort.unsorted();
        }

        var builder = Sort.builder();
        for (String s : params) {
            if (s == null) {
                continue;
            }

            var value = s.strip();
            if (value.isEmpty()) {
                continue;
            }
            boolean ascending = value.charAt(0) != '-';
            String paramName = ascending ? value : value.substring(1).strip();
            if (paramName.isEmpty()) {
                continue;
            }
            if (ascending) {
                builder.asc(paramName);
            } else {
                builder.desc(paramName);
            }
        }
        return builder.build();
    }

    /// 判断是否为空排序
    public boolean isUnsorted() {
        return orders.isEmpty();
    }

    // 排序方向枚举
    public enum Direction {
        /// 正序
        ASC,
        /// 倒序
        DESC
    }

    /// 构建器
    public static class Builder {

        Builder() {
            this.orders = new ArrayList<>();
        }

        /// 正序
        public Builder asc(String property) {
            orders.add(new Order(property, Direction.ASC));
            return this;
        }

        /// 倒序
        public Builder desc(String property) {
            orders.add(new Order(property, Direction.DESC));
            return this;
        }
    }

    /// 排序项
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Order {

        /// 排序属性
        private final String property;
        /// 排序方向
        private final Direction direction;

        private Order(String property, Direction direction) {
            this.property = normalizeProperty(property);
            this.direction = requireNonNull(direction, "排序方向不能为空");
        }

        /// 检查给定字符串是否与当前排序属性（忽略大小写）匹配。
        ///
        /// @param s 待比较的字符串
        /// @return 如果给定字符串与排序属性相匹配（忽略大小写），则返回 true；否则返回 false。
        public boolean equalsIgnoreCase(String s) {
            return property.equalsIgnoreCase(s);
        }

        /// 判断当前排序方向是否为正序。
        ///
        /// @return 如果当前排序方向是正序，则返回 true；否则返回 false。
        public boolean isAsc() {
            return direction == Direction.ASC;
        }

        /// 判断当前排序方向是否为倒序。
        ///
        /// @return 如果当前排序方向是倒序，则返回 true；否则返回 false。
        public boolean isDesc() {
            return direction == Direction.DESC;
        }
    }

    private static List<Order> normalizeOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return List.of();
        }

        var normalizedOrders = new ArrayList<Order>(orders.size());
        var uniqueProperties = new java.util.HashSet<String>(orders.size());
        for (Order order : orders) {
            if (uniqueProperties.add(normalizePropertyKey(order.getProperty()))) {
                normalizedOrders.add(order);
            }
        }
        return normalizedOrders;
    }

    private static String normalizeProperty(String property) {
        var normalized = requireNonNull(property, "排序属性不能为空").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("排序属性不能为空");
        }
        return normalized;
    }

    private static String normalizePropertyKey(String property) {
        return property.toUpperCase(Locale.ROOT);
    }
}
