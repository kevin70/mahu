package cool.houge.mahu.shared.repository.sys;

import com.github.f4b6a3.uuid.UuidCreator;
import cool.houge.mahu.entity.sys.DelayMessage;
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

/// 延迟消息
///
/// @author ZY (kzou227@qq.com)
@Service.Singleton
public class DelayMessageRepository extends HBeanRepository<UUID, DelayMessage> {

    private static final Logger log = LogManager.getLogger(DelayMessageRepository.class);

    public DelayMessageRepository(Database db) {
        super(DelayMessage.class, db);
    }

    /// 批量保存
    public void batchSave(DelayMessage message) {
        message.setId(UuidCreator.getTimeOrderedEpoch());

        var ctx = Contexts.context();
        if (ctx.isEmpty()) {
            this.save(message);
            log.debug("持久化延迟消息: {}", message);
        } else {
            ctx.get()
                    .get(DelayMessageHolder.class)
                    .ifPresentOrElse(
                            holder -> holder.add(message), () -> {
                                var cb = new DelayMessageHolder().add(message);
                                ctx.get().register(cb);
                                db().register(cb);
                            }
                            //
                            );
            log.debug("保存延迟消息到 Context: {}", message);
        }
    }

    private class DelayMessageHolder extends TransactionCallbackAdapter {

        List<DelayMessage> messages = new ArrayList<>(4);

        @Override
        public void preCommit() {
            if (!messages.isEmpty()) {
                DelayMessageRepository.this.saveAll(messages);
                log.debug("批量持久化延迟消息: {}", messages);
            }
        }

        @Override
        public void preRollback() {
            if (!messages.isEmpty()) {
                messages.clear();
            }
        }

        DelayMessageHolder add(DelayMessage message) {
            messages.add(message);
            return this;
        }
    }
}
