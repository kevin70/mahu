package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.ScheduledTaskLog;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class ScheduledExeLogRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<ScheduledTask> TASK_MODEL = Instancio.of(ScheduledTask.class)
            .ignore(field(ScheduledTask::getVersion))
            .toModel();

    private static final Model<ScheduledTaskLog> LOG_MODEL = Instancio.of(ScheduledTaskLog.class)
            .ignore(field(ScheduledTaskLog::getCreatedAt))
            .ignore(field(ScheduledTaskLog::getFailCause))
            .ignore(field(ScheduledTaskLog::getScheduledTask))
            .toModel();

    private ScheduledExeLogRepository repo() {
        return new ScheduledExeLogRepository(db());
    }

    @Test
    void findPage_filters_by_taskName_via_join() {
        var t1 = task("task1");
        var t2 = task("task2");
        db().saveAll(List.of(t1, t2));

        var l1 = log(t1, true, "a");
        var l2 = log(t1, false, "b");
        var l3 = log(t2, true, "c");
        db().saveAll(List.of(l1, l2, l3));

        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var onlyT1 = repo().findPage("task1", page);
        assertThat(onlyT1.getList()).extracting(l -> l.getScheduledTask().getTaskName()).containsOnly("task1");

        var all = repo().findPage(null, page);
        assertThat(all.getList().size()).isGreaterThanOrEqualTo(3);
    }

    private static ScheduledTaskLog log(ScheduledTask task, boolean success, String traceId) {
        var l = Instancio.of(LOG_MODEL).create();
        l.setId(UUID.randomUUID());
        l.setSuccess(success);
        l.setTraceId(traceId);
        l.setStartTime(Instant.parse("2020-01-01T00:00:00Z"));
        l.setDoneTime(Instant.parse("2020-01-01T00:00:01Z"));
        l.setPickedBy("tester");
        l.setScheduledTask(task);
        return l;
    }

    private static ScheduledTask task(String name) {
        var t = Instancio.of(TASK_MODEL).create();
        t.setTaskName(name);
        t.setPicked(false);
        t.setPriority(1);
        return t;
    }
}

