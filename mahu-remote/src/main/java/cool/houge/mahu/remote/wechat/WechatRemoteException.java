package cool.houge.mahu.remote.wechat;

import lombok.Getter;

/// 微信接口调用异常.
///
/// @author ZY (kzou227@qq.com)
@Getter
public class WechatRemoteException extends RuntimeException {

    /// 微信错误码.
    private final int code;

    public WechatRemoteException(int code, String message) {
        super(message);
        this.code = code;
    }
}
