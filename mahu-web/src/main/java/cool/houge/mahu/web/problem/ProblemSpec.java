package cool.houge.mahu.web.problem;

import java.util.Map;
import lombok.Data;

/// 错误语义定义
///
/// @author ZY (kzou227@qq.com)
@Data
public class ProblemSpec {

    /// 响应 HTTP 状态
    private Integer status;
    /// 错误代码
    private Integer code;
    /// 错误描述
    private String message;
    /// 详细信息
    private Map<String, Object> details;
}
