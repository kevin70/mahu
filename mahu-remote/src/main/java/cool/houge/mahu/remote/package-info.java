///
/// @author ZY (kzou227@qq.com)
@InjectModule(
        name = "MahuRemote",
        requires = {Config.class, ObjectMapper.class, WebClient.class})
package cool.houge.mahu.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.inject.InjectModule;
import io.helidon.common.config.Config;
import io.helidon.webclient.api.WebClient;
