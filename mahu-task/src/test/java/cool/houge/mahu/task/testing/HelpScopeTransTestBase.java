package cool.houge.mahu.task.testing;

import io.ebeaninternal.api.HelpScopeTrans;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/// 关闭 `@Transactional` 增强的测试基类。
public abstract class HelpScopeTransTestBase {

    @BeforeEach
    void disableHelpScopeTrans() {
        HelpScopeTrans.setEnabled(false);
    }

    @AfterEach
    void enableHelpScopeTrans() {
        HelpScopeTrans.setEnabled(true);
    }
}
