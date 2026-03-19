package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.query.ScheduledTaskQuery;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class ScheduledTaskRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<ScheduledTask> TASK_MODEL = Instancio.of(ScheduledTask.class)
            .ignore(field(ScheduledTask::getVersion))
            .toModel();

    private ScheduledTaskRepository repo() {
        return new ScheduledTaskRepository(db());
    }

    @Test
    void findForUpdate_returns_task_or_null() {
        var t = task("task1");
        db().save(t);

        assertThat(repo().findForUpdate("task1")).isNotNull();
        assertThat(repo().findForUpdate("missing")).isNull();
    }

    @Test
    void findPage_filters_by_taskName_icontains() {
        var paySync = task("pay.sync");
        var payReconcile = task("pay.reconcile");
        var userCleanup = task("user.cleanup");
        db().saveAll(List.of(paySync, payReconcile, userCleanup));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();
        var result = repo().findPage(ScheduledTaskQuery.builder().taskName("pay").build(), page);

        assertThat(result.getList()).extracting(ScheduledTask::getTaskName).contains("pay.sync", "pay.reconcile");
        assertThat(result.getList()).extracting(ScheduledTask::getTaskName).doesNotContain("user.cleanup");
    }

    private static ScheduledTask task(String name) {
        var t = Instancio.of(TASK_MODEL).create();
        t.setTaskName(name);
        t.setPicked(false);
        t.setPriority(1);
        return t;
    }
}

