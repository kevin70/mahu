package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import cool.houge.mahu.Status;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.testing.PostgresLiquibaseTestBase;
import java.time.Instant;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Model;
import org.junit.jupiter.api.Test;

class DelayedTaskRepositoryTest extends PostgresLiquibaseTestBase {

    private static final Model<DelayedTask> TASK_MODEL = Instancio.of(DelayedTask.class)
            .ignore(field(DelayedTask::getId))
            .ignore(field(DelayedTask::getCreatedAt))
            .ignore(field(DelayedTask::getUpdatedAt))
            .toModel();

    private DelayedTaskRepository repo() {
        return new DelayedTaskRepository(db());
    }

    @Test
    void batchSave_without_context_persists_immediately() {
        var t = task("topic1");
        t.setMaxAttempts(3);
        t.setPayload("{\"k\":\"v\"}");
        t.setIdempotencyKey("k1");

        t.setReferenceId("ref-1");
        repo().enqueueDelayedTask(t);

        assertThat(t.getId()).isNotNull();
        var loaded = db().find(DelayedTask.class, t.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded.getReferenceId()).isEqualTo("ref-1");
    }

    @Test
    void findExpiredProcessing_respects_per_task_lease_seconds() {
        var t0 = Instant.parse("2025-06-01T12:00:00Z");
        var stillWithinLease = task("within-lease");
        stillWithinLease.setStatus(Status.PROCESSING.getCode());
        stillWithinLease.setLockAt(t0.minusSeconds(100));
        stillWithinLease.setLeaseSeconds(500);

        var leaseExpired = task("expired");
        leaseExpired.setStatus(Status.PROCESSING.getCode());
        leaseExpired.setLockAt(t0.minusSeconds(100));
        leaseExpired.setLeaseSeconds(30);

        db().saveAll(List.of(stillWithinLease, leaseExpired));

        var found = repo().findExpiredProcessing(t0, 20);
        assertThat(found).extracting(DelayedTask::getTopic).containsOnly("expired");
    }

    @Test
    void findPage_filters_by_topic() {
        var t1a = task("t1");
        var t1b = task("t1b");
        var t2 = task("t2");
        db().saveAll(List.of(t1a, t1b, t2));
        var page = Page.builder().page(1).pageSize(20).includeTotal(true).build();

        var all = repo().findPage(null, page);
        assertThat(all.getList().size()).isGreaterThanOrEqualTo(3);

        var onlyT1 = repo().findPage("t1", page);
        assertThat(onlyT1.getList()).extracting(DelayedTask::getTopic).containsOnly("t1");
    }

    private static DelayedTask task(String topic) {
        var t = Instancio.of(TASK_MODEL).create();
        t.setTopic(topic);
        t.setStatus(1);
        t.setDelayUntil(Instant.parse("2030-01-01T00:00:00Z"));
        t.setAttempts(0);
        t.setMaxAttempts(1);
        t.setLeaseSeconds(30);
        t.setPayload("{}");
        t.setIdempotencyKey(topic + "_k");
        t.setReferenceId("ref:" + topic);
        return t;
    }
}
