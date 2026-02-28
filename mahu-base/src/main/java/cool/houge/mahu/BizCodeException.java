package cool.houge.mahu;

import com.google.common.base.Strings;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

/// 业务错误码异常
///
/// 用于表示业务逻辑中出现的错误，包含业务错误码和错误描述信息
///
/// @author ZY (kzou227@qq.com)
@Getter
public final class BizCodeException extends RuntimeException {

    /// 业务错误码
    private final BizCode code;

    /// 使用业务码构建异常
    ///
    /// @param code 业务码
    public BizCodeException(@NonNull BizCode code) {
        super(code.message());
        this.code = code;
    }

    /// 使用业务码和自定义描述构建异常
    ///
    /// @param code    业务码
    /// @param message 自定义描述
    public BizCodeException(@NonNull BizCode code, @NonNull String message) {
        super(message);
        this.code = code;
    }

    /// 使用业务码和模板描述构建异常
    ///
    /// @param code     业务码
    /// @param template 描述模板
    /// @param args     模板参数
    public BizCodeException(@NonNull BizCode code, @NonNull String template, Object... args) {
        super(Strings.lenientFormat(template, args));
        this.code = code;
    }

    /// 返回格式化的错误描述
    ///
    /// 格式为：错误码: 错误描述
    ///
    /// @return 格式化的错误描述
    @Override
    public String getMessage() {
        return this.code.code() + ": " + super.getMessage();
    }

    /// 返回原始的错误描述
    ///
    /// 不包含错误码前缀，仅返回错误描述本身
    ///
    /// @return 原始错误描述
    public String getRawMessage() {
        return super.getMessage();
    }

    /// 返回格式化的错误描述
    ///
    /// @return 格式化的错误描述
    @Override
    public String toString() {
        return this.getMessage();
    }
}
