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

import cool.houge.mahu.domain.Sort.Direction;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * Basic Java Bean implementation of {@link Pageable}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Anastasiia Smirnova
 * @author Mark Paluch
 * @author Thach Le
 */
public class PageRequest extends AbstractPageRequest {

    private static final long serialVersionUID = -4541509938956089562L;

    private final Sort sort;

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param pageNumber zero-based page number, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    protected PageRequest(int pageNumber, int pageSize, Sort sort) {
        super(pageNumber, pageSize);
        Objects.requireNonNull(sort, "Sort must not be null");
        this.sort = sort;
    }

    /**
     * Creates a new unsorted {@link PageRequest}.
     *
     * @param pageNumber zero-based page number, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @since 2.0
     */
    public static PageRequest of(int pageNumber, int pageSize) {
        return of(pageNumber, pageSize, Sort.unsorted());
    }

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param pageNumber zero-based page number, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     * @since 2.0
     */
    public static PageRequest of(int pageNumber, int pageSize, Sort sort) {
        return new PageRequest(pageNumber, pageSize, sort);
    }

    /**
     * Creates a new {@link PageRequest} with sort direction and properties applied.
     *
     * @param pageNumber zero-based page number, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @since 2.0
     */
    public static PageRequest of(int pageNumber, int pageSize, Direction direction, String... properties) {
        return of(pageNumber, pageSize, Sort.by(direction, properties));
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    /**
     * Creates a new {@link PageRequest} with {@link Direction} and {@code properties} applied.
     *
     * @param direction must not be {@literal null}.
     * @param properties must not be {@literal null}.
     * @return a new {@link PageRequest}.
     * @since 2.5
     */
    public PageRequest withSort(Direction direction, String... properties) {
        return new PageRequest(getPageNumber(), getPageSize(), Sort.by(direction, properties));
    }

    /**
     * Creates a new {@link PageRequest} with {@link Sort} applied.
     *
     * @param sort must not be {@literal null}.
     * @return a new {@link PageRequest}.
     * @since 2.5
     */
    public PageRequest withSort(Sort sort) {
        return new PageRequest(getPageNumber(), getPageSize(), sort);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PageRequest that)) {
            return false;
        }
        return super.equals(that) && sort.equals(that.sort);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + sort.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Page request [number: %d, size %d, sort: %s]", getPageNumber(), getPageSize(), sort);
    }
}
