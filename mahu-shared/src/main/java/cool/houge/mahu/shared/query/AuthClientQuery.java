package cool.houge.mahu.shared.query;

import cool.houge.mahu.entity.TerminalType;
import lombok.Builder;
import lombok.Value;

/// 认证客户端查询条件
///
/// @author ZY (kzou227@qq.com)
@Value
@Builder
public class AuthClientQuery {

    /// 客户端 ID
    String clientId;
    /// 终端类型
    TerminalType terminalType;
    /// 微信应用 ID
    String wechatAppid;
}
