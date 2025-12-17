package cool.houge.mahu.admin;

import cool.houge.mahu.admin.security.AuthContext;
import io.ebean.config.CurrentUserProvider;

/// 上下文用户提供者
///
/// @author ZY (kzou227@qq.com)
public class ContextCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Object currentUser() {
        return AuthContext.current().adminId();
    }
}
