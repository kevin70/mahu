package cool.houge.mahu.admin.controller;

import cool.houge.mahu.admin.internal.VoBeanMapper;
import cool.houge.mahu.admin.service.SettingService;
import cool.houge.mahu.web.WebSupport;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/// 系统基础接口
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SettingController implements HttpService, WebSupport {

    private final VoBeanMapper beanMapper;
    private final SettingService settingService;

    @Inject
    public SettingController(VoBeanMapper beanMapper, SettingService settingService) {
        this.beanMapper = beanMapper;
        this.settingService = settingService;
    }

    @Override
    public void routing(HttpRules rules) {
        //
    }
}
