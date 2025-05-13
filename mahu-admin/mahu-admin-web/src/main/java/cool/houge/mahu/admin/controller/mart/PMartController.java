package cool.houge.mahu.admin.controller.mart;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 公共数据接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class PMartController implements HttpService {

    private final VoBeanMapper beanMapper;

    @Inject
    public PMartController(VoBeanMapper beanMapper) {
        this.beanMapper = beanMapper;
    }

    @Override
    public void routing(HttpRules rules) {
        //
    }
}
