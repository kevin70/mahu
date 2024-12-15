package cool.houge.mahu;

import cool.houge.mahu.security.AuthContext;
import io.ebean.config.CurrentUserProvider;

/// 上下文用户提供者
///
/// @author ZY (kzou227@qq.com)
public class ContextCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Object currentUser() {
        return AuthContext.get().uid();
    }
}
