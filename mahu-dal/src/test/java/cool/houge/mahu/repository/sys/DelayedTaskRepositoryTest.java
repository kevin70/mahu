package cool.houge.mahu.repository.sys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

import cool.houge.mahu.config.Status;
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

    @Test
    void enqueueDelayedTask_rejects_blank_reference_id() {
        var t = task("topic-blank-ref");
        t.setReferenceId(" ");

        assertThatThrownBy(() -> repo().enqueueDelayedTask(t))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("referenceId");
    }

    @Test
    void findDuePending_only_returns_due_pending_tasks() {
        var now = Instant.parse("2025-06-01T12:00:00Z");
        var duePending = task("due");
        duePending.setStatus(Status.PENDING.getCode());
        duePending.setDelayUntil(now.minusSeconds(1));

        var futurePending = task("future");
        futurePending.setStatus(Status.PENDING.getCode());
        futurePending.setDelayUntil(now.plusSeconds(60));

        var dueProcessing = task("processing");
        dueProcessing.setStatus(Status.PROCESSING.getCode());
        dueProcessing.setDelayUntil(now.minusSeconds(1));

        db().saveAll(List.of(duePending, futurePending, dueProcessing));

        var found = repo().findDuePending(now, 20);
        assertThat(found).extracting(DelayedTask::getTopic).containsOnly("due");
    }

    @Test
    void claimPending_updates_status_lock_and_attempts_only_when_due_pending() {
        // 验证领取成功路径：仅到期的 PENDING 才能被原子迁移到 PROCESSING。
        var now = Instant.parse("2025-06-01T12:00:00Z");
        var t = task("claimable");
        t.setStatus(Status.PENDING.getCode());
        t.setDelayUntil(now.minusSeconds(5));
        t.setAttempts(0);
        db().save(t);

        var claimed = repo().claimPending(t.getId(), now, 120, 1);
        assertThat(claimed).isTrue();

        var loaded = db().find(DelayedTask.class, t.getId());
        assertThat(loaded.getStatus()).isEqualTo(Status.PROCESSING.getCode());
        assertThat(loaded.getLockAt()).isEqualTo(now);
        assertThat(loaded.getLeaseSeconds()).isEqualTo(120);
        assertThat(loaded.getAttempts()).isEqualTo(1);
    }

    @Test
    void claimPending_returns_false_when_not_due() {
        // 验证领取失败路径：未到期任务不会被错误领取。
        var now = Instant.parse("2025-06-01T12:00:00Z");
        var t = task("not-due");
        t.setStatus(Status.PENDING.getCode());
        t.setDelayUntil(now.plusSeconds(60));
        t.setLockAt(null);
        db().save(t);

        var claimed = repo().claimPending(t.getId(), now, 120, 1);
        assertThat(claimed).isFalse();

        var loaded = db().find(DelayedTask.class, t.getId());
        assertThat(loaded.getStatus()).isEqualTo(Status.PENDING.getCode());
        assertThat(loaded.getLockAt()).isNull();
    }

    @Test
    void complete_only_transitions_from_processing() {
        // 验证终态迁移前置条件：仅 PROCESSING 可迁移到 COMPLETED。
        var processing = task("processing-complete");
        processing.setStatus(Status.PROCESSING.getCode());
        processing.setLockAt(Instant.parse("2025-06-01T11:59:00Z"));
        db().save(processing);

        var pending = task("pending-no-complete");
        pending.setStatus(Status.PENDING.getCode());
        db().save(pending);

        repo().complete(processing.getId());
        repo().complete(pending.getId());

        var done = db().find(DelayedTask.class, processing.getId());
        assertThat(done.getStatus()).isEqualTo(Status.COMPLETED.getCode());
        assertThat(done.getLockAt()).isNull();
        assertThat(done.getDelayUntil()).isNull();

        var untouched = db().find(DelayedTask.class, pending.getId());
        assertThat(untouched.getStatus()).isEqualTo(Status.PENDING.getCode());
    }

    @Test
    void archive_only_transitions_from_processing() {
        // 验证终态迁移前置条件：仅 PROCESSING 可迁移到 ARCHIVED。
        var processing = task("processing-archive");
        processing.setStatus(Status.PROCESSING.getCode());
        processing.setLockAt(Instant.parse("2025-06-01T11:59:00Z"));
        db().save(processing);

        var pending = task("pending-no-archive");
        pending.setStatus(Status.PENDING.getCode());
        db().save(pending);

        repo().archive(processing.getId());
        repo().archive(pending.getId());

        var archived = db().find(DelayedTask.class, processing.getId());
        assertThat(archived.getStatus()).isEqualTo(Status.ARCHIVED.getCode());
        assertThat(archived.getLockAt()).isNull();
        assertThat(archived.getDelayUntil()).isNull();

        var untouched = db().find(DelayedTask.class, pending.getId());
        assertThat(untouched.getStatus()).isEqualTo(Status.PENDING.getCode());
    }

    @Test
    void transitionExpiredToPending_updates_only_when_lease_expired() {
        // 验证租约超时回退：只有超时的 PROCESSING 才会回到 PENDING。
        var now = Instant.parse("2025-06-01T12:00:00Z");
        var expired = task("expired-to-pending");
        expired.setStatus(Status.PROCESSING.getCode());
        expired.setLockAt(now.minusSeconds(120));
        expired.setLeaseSeconds(30);
        db().save(expired);

        var withinLease = task("within-lease-no-transition");
        withinLease.setStatus(Status.PROCESSING.getCode());
        withinLease.setLockAt(now.minusSeconds(10));
        withinLease.setLeaseSeconds(30);
        db().save(withinLease);

        var nextRetryAt = now.plusSeconds(90);
        var transitioned = repo().transitionExpiredToPending(expired.getId(), now, nextRetryAt);
        var notTransitioned = repo().transitionExpiredToPending(withinLease.getId(), now, nextRetryAt);
        assertThat(transitioned).isTrue();
        assertThat(notTransitioned).isFalse();

        var transitionedTask = db().find(DelayedTask.class, expired.getId());
        assertThat(transitionedTask.getStatus()).isEqualTo(Status.PENDING.getCode());
        assertThat(transitionedTask.getDelayUntil()).isEqualTo(nextRetryAt);
        assertThat(transitionedTask.getLockAt()).isNull();

        var untouched = db().find(DelayedTask.class, withinLease.getId());
        assertThat(untouched.getStatus()).isEqualTo(Status.PROCESSING.getCode());
    }

    @Test
    void transitionExpiredToFailed_updates_only_when_lease_expired() {
        // 验证超时失败迁移：未超时任务保持 PROCESSING 不变。
        var now = Instant.parse("2025-06-01T12:00:00Z");
        var expired = task("expired-to-failed");
        expired.setStatus(Status.PROCESSING.getCode());
        expired.setLockAt(now.minusSeconds(120));
        expired.setLeaseSeconds(30);
        db().save(expired);

        var withinLease = task("within-lease-no-fail");
        withinLease.setStatus(Status.PROCESSING.getCode());
        withinLease.setLockAt(now.minusSeconds(10));
        withinLease.setLeaseSeconds(30);
        db().save(withinLease);

        var transitioned = repo().transitionExpiredToFailed(expired.getId(), now);
        var notTransitioned = repo().transitionExpiredToFailed(withinLease.getId(), now);
        assertThat(transitioned).isTrue();
        assertThat(notTransitioned).isFalse();

        var failed = db().find(DelayedTask.class, expired.getId());
        assertThat(failed.getStatus()).isEqualTo(Status.FAILED.getCode());
        assertThat(failed.getDelayUntil()).isNull();
        assertThat(failed.getLockAt()).isNull();

        var untouched = db().find(DelayedTask.class, withinLease.getId());
        assertThat(untouched.getStatus()).isEqualTo(Status.PROCESSING.getCode());
    }

    private static DelayedTask task(String topic) {
        var t = Instancio.of(TASK_MODEL).create();
        t.setTopic(topic);
        t.setStatus(Status.PENDING.getCode());
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
