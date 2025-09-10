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

/// 分页信息的抽象接口
///
/// @author ZY (kzou227@qq.com)
public interface Pageable {

    /// 返回当前[Pageable]是否不包含分页信息
    boolean isUnpaged();

    /// 返回要查询的页码
    ///
    /// @return 要返回的页码，如果对象是[#isUnpaged]，则抛出[UnsupportedOperationException]
    /// @throws UnsupportedOperationException 如果对象是[#isUnpaged]
    int getPageNumber();

    /// 返回每页要返回的条目数
    ///
    /// @return 该页的条目数，如果对象是[#isUnpaged]，则抛出[UnsupportedOperationException]
    /// @throws UnsupportedOperationException 如果对象是[#isUnpaged]
    int getPageSize();

    /// 根据基础页码和页面大小返回要获取的偏移量
    ///
    /// @return 要获取的偏移量，如果对象是[#isUnpaged]，则抛出[UnsupportedOperationException]
    /// @throws UnsupportedOperationException 如果对象是[#isUnpaged]
    long getOffset();

    /**
     * 返回排序参数
     */
    Sort getSort();

    /// 返回一个表示无分页设置但具有[Sort]的[Pageable]实例
    ///
    /// @param sort 不能为`null`，必要时使用[Sort#unsorted]
    /// @return 从不为`null`
    static Pageable unpaged(Sort sort) {
        return new Unpaged(sort);
    }
}
