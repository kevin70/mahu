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

import java.util.Objects;

/**
 * Abstract interface for pagination information.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Christoph Strobl
 */
public interface Pageable {

    /**
     * Returns a {@link Pageable} instance representing no pagination setup.
     */
    static Pageable unpaged() {
        return unpaged(Sort.unsorted());
    }

    /**
     * Returns a {@link Pageable} instance representing no pagination setup having a defined result {@link Sort order}.
     *
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} if needed.
     * @return never {@literal null}.
     * @since 3.2
     */
    static Pageable unpaged(Sort sort) {
        return Unpaged.sorted(sort);
    }

    /**
     * Creates a new {@link Pageable} for the first page (page number {@code 0}) given {@code pageSize} .
     *
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @return a new {@link Pageable}.
     * @since 2.5
     */
    static Pageable ofSize(int pageSize) {
        return PageRequest.of(0, pageSize);
    }

    /**
     * Returns whether the current {@link Pageable} contains pagination information.
     */
    default boolean isPaged() {
        return true;
    }

    /**
     * Returns whether the current {@link Pageable} does not contain pagination information.
     */
    default boolean isUnpaged() {
        return !isPaged();
    }

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned or throws {@link UnsupportedOperationException} if the object is
     *         {@link #isUnpaged()}.
     * @throws UnsupportedOperationException if the object is {@link #isUnpaged()}.
     */
    int getPageNumber();

    /**
     * Returns the number of items to be returned.
     *
     * @return the number of items of that page or throws {@link UnsupportedOperationException} if the object is
     *         {@link #isUnpaged()}.
     * @throws UnsupportedOperationException if the object is {@link #isUnpaged()}.
     */
    int getPageSize();

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     *
     * @return the offset to be taken or throws {@link UnsupportedOperationException} if the object is
     *         {@link #isUnpaged()}.
     * @throws UnsupportedOperationException if the object is {@link #isUnpaged()}.
     */
    long getOffset();

    /**
     * Returns the sorting parameters.
     */
    Sort getSort();

    /**
     * Returns the current {@link Sort} or the given one if the current one is unsorted.
     *
     * @param sort must not be {@literal null}.
     */
    default Sort getSortOr(Sort sort) {
        Objects.requireNonNull(sort, "Fallback Sort must not be null");
        return getSort().isSorted() ? getSort() : sort;
    }
}
