package cool.houge.mahu.admin;

import io.helidon.config.Config;
import org.junit.jupiter.api.Test;

///
/// @author ZY (kzou227@qq.com)
class LombokDelegateTest {

    @Test
    void config() {
        var config = Config.create();
        var infoConfig = new InfoConfigProxy(config);
        System.out.println(infoConfig);
        System.out.println(infoConfig.name());
        System.out.println(infoConfig.version());
    }
}
