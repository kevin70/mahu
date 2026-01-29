package cool.houge.mahu.shared.repository.sys;

import com.github.f4b6a3.ulid.UlidFactory;
import cool.houge.mahu.entity.sys.DelayedTask;
import cool.houge.mahu.util.HBeanRepository;
import io.ebean.Database;
import io.ebean.TransactionCallbackAdapter;
import io.helidon.common.context.Contexts;
import io.helidon.service.registry.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 延迟任务
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DelayedTaskRepository extends HBeanRepository<UUID, DelayedTask> {

    private static final Logger log = LogManager.getLogger(DelayedTaskRepository.class);
    private static final UlidFactory ULID_FACTORY = UlidFactory.newMonotonicInstance();

    public DelayedTaskRepository(Database db) {
        super(DelayedTask.class, db);
    }

    /// 批量保存
    public void batchSave(DelayedTask bean) {
        bean.setId(ULID_FACTORY.create().toUuid());

        var ctx = Contexts.context();
        if (ctx.isEmpty()) {
            this.save(bean);
            log.debug("持久化延迟任务: {}", bean);
        } else {
            ctx.get()
                    .get(DelayedTaskHolder.class)
                    .ifPresentOrElse(
                            holder -> holder.add(bean), () -> {
                                var cb = new DelayedTaskHolder().add(bean);
                                ctx.get().register(cb);
                                db().register(cb);
                            }
                            //
                            );
            log.debug("保存延迟任务到 Context: {}", bean);
        }
    }

    private class DelayedTaskHolder extends TransactionCallbackAdapter {

        List<DelayedTask> tasks = new ArrayList<>(4);

        @Override
        public void preCommit() {
            if (!tasks.isEmpty()) {
                DelayedTaskRepository.this.saveAll(tasks);
                log.debug("批量持久化延迟任务: {}", tasks);
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
