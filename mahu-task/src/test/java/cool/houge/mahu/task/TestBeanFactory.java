package cool.houge.mahu.task;

import com.github.kagkarlsson.scheduler.Scheduler;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.test.TestScope;
import org.mockito.Mockito;

/// 测试对象定义工厂
///
/// @author ZY (kzou227@qq.com)
@TestScope
@Factory
public class TestBeanFactory {

    @Bean
    public Scheduler scheduler() {
        return Mockito.mock(Scheduler.class);
    }
}
