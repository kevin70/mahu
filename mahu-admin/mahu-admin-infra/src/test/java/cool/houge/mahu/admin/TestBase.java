package cool.houge.mahu.admin;

import io.avaje.inject.test.InjectTest;
import io.helidon.common.context.Contexts;
import io.helidon.testing.junit5.Testing;
import org.junit.jupiter.api.BeforeEach;

/// 测试基类
///
/// @author ZY (kzou227@qq.com)
@InjectTest
@Testing.Test
public abstract class TestBase {

    @BeforeEach
    void beforeEach() {
        var ctx = Contexts.context();
        ctx.ifPresent(context -> context.register(TestAuthContext.DEFAULT));
    }
}
