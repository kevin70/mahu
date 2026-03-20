package cool.houge.mahu.task;

import com.github.f4b6a3.ulid.UlidCreator;
import com.github.kagkarlsson.scheduler.event.ExecutionChain;
import com.github.kagkarlsson.scheduler.event.ExecutionInterceptor;
import com.github.kagkarlsson.scheduler.task.CompletionHandler;
import com.github.kagkarlsson.scheduler.task.ExecutionContext;
import com.github.kagkarlsson.scheduler.task.TaskInstance;
import cool.houge.mahu.G;
import cool.houge.mahu.entity.sys.ScheduledTask;
import cool.houge.mahu.entity.sys.ScheduledTaskLog;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import io.helidon.logging.common.HelidonMdc;
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
        return G.withTraceId(() -> {
            var startTime = Instant.now();
            var traceId = HelidonMdc.get(G.MDC_TRACE_ID).orElseThrow();
            try {
                var rs = chain.proceed(taskInstance, executionContext);
                log.info("任务执行成功");
                this.saveExecutionLog(executionContext, traceId, startTime, true);
                return rs;
            } catch (Exception e) {
                this.saveExecutionLog(executionContext, traceId, startTime, false);
                log.error("任务执行失败", e);
                throw e;
            }
        });
    }

    @Transactional
    void saveExecutionLog(ExecutionContext context, String traceId, Instant startedAt, boolean success) {
        try {
            var execution = context.getExecution();
            var task = db.reference(ScheduledTask.class, execution.getTaskName());
            var bean = new ScheduledTaskLog()
                    .setId(UlidCreator.getUlid().toUuid())
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
