package cool.houge.mahu.task.handler;

/// 延时任务 topic 处理器：不负责 delayed_task 状态落库（由 Worker 统一完成）。
public interface DelayedTaskHandler {

    boolean supports(String topic);

    DelayedTaskCompletionResult handle(ClaimedDelayedTask task);
}
