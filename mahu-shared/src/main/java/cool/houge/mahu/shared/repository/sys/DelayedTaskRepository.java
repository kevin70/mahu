package cool.houge.mahu.shared.repository.sys;

import com.github.f4b6a3.ulid.UlidFactory;
import cool.houge.mahu.domain.Page;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.entity.sys.query.QDelayedTask;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.PagedList;
import io.ebean.TransactionCallbackAdapter;
import io.helidon.common.context.Contexts;
import io.helidon.service.registry.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延时任务
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DelayedTaskRepository extends HBeanRepository<UUID, DelayedTask> {

    private static final Logger log = LogManager.getLogger(DelayedTaskRepository.class);
    private static final UlidFactory ULID_FACTORY = UlidFactory.newMonotonicInstance();

    public DelayedTaskRepository(Database db) {
        super(DelayedTask.class, db);
    }

    /// 批量保存延时任务
    ///
    /// 如果在事务上下文中，任务会被暂存，在事务提交时批量保存
    /// 如果不在事务上下文中，任务会立即保存
    ///
    /// @param bean 延时任务实体
    public void batchSave(DelayedTask bean) {
        bean.setId(ULID_FACTORY.create().toUuid());

        var ctx = Contexts.context();
        if (ctx.isEmpty()) {
            this.save(bean);
            log.debug("持久化延时任务: {}", bean);
        } else {
            ctx.get().get(DelayedTaskHolder.class).ifPresentOrElse(holder -> holder.add(bean), () -> {
                var cb = new DelayedTaskHolder(this).add(bean);
                ctx.get().register(cb);
                db().register(cb);
            });
            log.debug("保存延时任务到 Context: {}", bean);
        }
    }

    /// 分页查询延时任务
    ///
    /// @param page 分页参数
    /// @return 分页结果
    public PagedList<DelayedTask> findPage(@Nullable String topic, Page page) {
        var qb = new QDelayedTask(db());
        if (topic != null && !topic.isBlank()) {
            qb.topic.eq(topic);
        }
        return super.findPage(qb, page);
    }

    /// 延时任务持有者，用于在事务中暂存任务
    private static class DelayedTaskHolder extends TransactionCallbackAdapter {

        private final DelayedTaskRepository repository;
        private final List<DelayedTask> tasks = new ArrayList<>(4);

        DelayedTaskHolder(DelayedTaskRepository repository) {
            this.repository = repository;
        }

        @Override
        public void preCommit() {
            if (!tasks.isEmpty()) {
                repository.saveAll(tasks);
                log.debug("批量持久化延时任务: {}", tasks);
            }
        }

        @Override
        public void preRollback() {
            if (!tasks.isEmpty()) {
                tasks.clear();
            }
        }

        DelayedTaskHolder add(DelayedTask message) {
            tasks.add(message);
            return this;
        }
    }
}
