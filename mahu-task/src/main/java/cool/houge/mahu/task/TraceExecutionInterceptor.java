package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.event.ExecutionChain;
import com.github.kagkarlsson.scheduler.event.ExecutionInterceptor;
import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import cool.houge.mahu.TraceIdGenerator;
import cool.houge.mahu.task.entity.ScheduledTask;
import cool.houge.mahu.task.entity.ScheduledTaskExeLog;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import io.helidon.logging.common.HelidonMdc;
import io.hypersistence.tsid.TSID;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 日志追踪拦截器
///
/// @author ZY (kzou227@qq.com)
@RequiredArgsConstructor
public class TraceExecutionInterceptor implements ExecutionInterceptor {

    private static final Logger log = LogManager.getLogger(TraceExecutionInterceptor.class);
    private final Database db;

    @Override
    public CompletionHandler<?> execute(
            TaskInstance<?> taskInstance, ExecutionContext executionContext, ExecutionChain chain) {
        var traceId = TraceIdGenerator.generate();
        var startTime = Instant.now();

        try {
            HelidonMdc.set("traceId", traceId);
            var rs = chain.proceed(taskInstance, executionContext);
            log.info("任务执行成功");
            this.saveExecutionLog(executionContext, traceId, startTime, true);
            return rs;
        } catch (Throwable e) {
            this.saveExecutionLog(executionContext, traceId, startTime, false);
            log.error("任务执行失败", e);
            throw e;
        } finally {
            HelidonMdc.remove("traceId");
        }
    }

    @Transactional
    void saveExecutionLog(ExecutionContext context, String traceId, Instant startedAt, boolean success) {
        try {
            var execution = context.getExecution();
            var task = db.reference(ScheduledTask.class, execution.getTaskName());
            var bean = new ScheduledTaskExeLog()
                    .setId(TSID.fast().toLong())
                    .setScheduledTask(task)
                    .setPickedBy(execution.pickedBy)
                    .setStartTime(startedAt)
                    .setDoneTime(Instant.now())
                    .setTraceId(traceId)
                    .setSuccess(success);
            db.save(bean);
        } catch (Exception e) {
            log.error("保存任务执行记录失败", e);
        }
    }
}
