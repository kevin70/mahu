package cool.houge.mahu;

import io.ebean.Database;
import io.ebean.Transaction;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/// 事务测试基类
///
/// @author ZY (kzou227@qq.com)
public abstract class TestTransactionBase extends TestBase {

    @Inject
    Database database;

    protected boolean rollbackOnly = true;
    private Transaction transaction;

    @BeforeEach
    void before() {
        transaction = getDatabase().beginTransaction();
    }

    @AfterEach
    void after() {
        if (rollbackOnly) {
            transaction.rollback();
        } else {
            transaction.commit();
        }
    }

    /// 禁用事务自动回滚
    protected void disableRollback() {
        this.rollbackOnly = false;
    }

    protected Database getDatabase() {
        return database;
    }
}
