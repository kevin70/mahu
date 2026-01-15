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

import io.helidon.common.parameters.Parameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

/// 排序信息封装，提供流畅的 API 和不可变特性
///
/// @author ZY (kzou227@qq.com)
@Getter
@ToString
@EqualsAndHashCode
public class Sort {

    /// 排序集合
    private final List<Order> orders;

    private Sort(List<Order> orders) {
        this.orders = orders;
    }

    /// 排序构建器
    public static Builder builder() {
        return new Builder();
    }

    /// 空排序
    public static Sort unsorted() {
        return new Sort(Collections.emptyList());
    }

    /// 根据传入的参数创建一个排序对象。
    ///
    /// 参数中可包含以下键值对：
    /// - `sort`：排序参数。
    ///
    /// @param params 包含排序参数的对象。
    /// @return 排序
    public static @NonNull Sort of(@NonNull Parameters params) {
        if (!params.contains("sort")) {
            return Sort.unsorted();
        }
        return of(params.all("sort"));
    }

    /// 将排序参数转换为 Sort 对象
    ///
    /// @param params 排序参数列表
    public static @NonNull Sort of(@NonNull List<String> params) {
        if (params.isEmpty()) {
            return Sort.unsorted();
        }

        var builder = Sort.builder();
        for (String s : params) {
            if (s == null || s.isEmpty()) {
                continue;
            }

            boolean ascending = s.charAt(0) != '-';
            String paramName = ascending ? s : s.substring(1);
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

    // 排序项
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Order {

        /// 排序属性
        private final String property;
        /// 排序方向
        private final Direction direction;

        private Order(String property, Direction direction) {
            this.property = requireNonNull(property, "排序属性不能为空");
            this.direction = requireNonNull(direction, "排序方向不能为空");
        }
    }

    public static class Builder {

        private final List<Order> orders = new ArrayList<>();

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

        public Sort build() {
            return new Sort(List.copyOf(orders));
        }
    }
}
