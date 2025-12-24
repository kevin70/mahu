/*
 * Copyright 2017-2025 the original author or authors.
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

/// 无分页实现类，单例模式
///
/// @author ZY (kzou227@qq.com)
public class Unpaged implements Pageable {

    private final Sort sort;

    /// 带排序的私有构造器
    Unpaged(Sort sort) {
        this.sort = requireNonNull(sort);
    }

    @Override
    public boolean isUnpaged() {
        return true;
    }

    @Override
    public int getPageNumber() {
        throw new UnsupportedOperationException("无分页模式不支持获取页码");
    }

    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException("无分页模式不支持获取页大小");
    }

    @Override
    public long getOffset() {
        throw new UnsupportedOperationException("无分页模式不支持获取偏移量");
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable checkPageAndSize(int maxPage, long maxSize) {
        return this;
    }
}
