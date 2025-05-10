package cool.houge.mahu.task.impl;

import io.avaje.inject.test.InjectTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
///
/// @author ZY (kzou227@qq.com)
@InjectTest
class HelloTaskTest {

    @Inject
    HelloTask helloTask;

    @Test
    void execute() {
        System.out.println(helloTask);
    }

}
