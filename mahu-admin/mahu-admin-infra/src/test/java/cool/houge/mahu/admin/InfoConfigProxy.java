package cool.houge.mahu.admin;

import cool.houge.mahu.config.InfoConfig;
import io.helidon.config.Config;
import lombok.experimental.Delegate;

///
/// @author ZY (kzou227@qq.com)
public class InfoConfigProxy implements InfoConfig {

    @Delegate
    private InfoConfig infoConfig;

    public InfoConfigProxy(Config root) {
        var c = root.get("info");
        c.onChange(c2 -> {
            //
            System.out.println(c2);
            System.out.println("配置刷新");
        });
        infoConfig = InfoConfig.create(c);
    }
}
