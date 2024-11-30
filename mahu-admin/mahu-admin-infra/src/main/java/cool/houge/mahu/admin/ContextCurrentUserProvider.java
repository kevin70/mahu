package cool.houge.mahu.admin;

import cool.houge.lang.BizCodeException;
import cool.houge.lang.BizCodes;
import cool.houge.mahu.common.security.AuthContext;
import io.ebean.config.CurrentUserProvider;
import io.helidon.common.context.Context;
import io.helidon.common.context.Contexts;

/// 上下文用户提供者
///
/// @author ZY (kzou227@qq.com)
public class ContextCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Object currentUser() {
        return Contexts.context()
                .map(this::takeUid)
                .orElseThrow(() -> new BizCodeException(BizCodes.INTERNAL, "缺少 Context 对象"));
    }

    Long takeUid(Context ctx) {
        return ctx.get(AuthContext.class)
                .map(AuthContext::uid)
                .orElseThrow(() -> new BizCodeException(BizCodes.INTERNAL, "缺少 AuthContext"));
    }
}
