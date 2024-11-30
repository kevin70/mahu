package cool.houge.mahu.common;

import lombok.Data;

import java.util.List;

/// 分页响应
///
/// @author ZY (kzou227@qq.com)
@Data
public class PageResponse<T> {

    /// 总记录数
    private Integer totalCount;
    /// 记录列表
    private List<T> items;
}
