package cool.houge.mahu;

import com.github.f4b6a3.ulid.UlidCreator;
import com.google.common.base.Splitter;
import cool.houge.mahu.common.Metadata;
import io.helidon.common.configurable.Resource;
import io.helidon.common.media.type.MediaTypes;
import io.helidon.http.HeaderName;
import io.helidon.http.HeaderNames;
import io.helidon.webserver.http.*;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;

import static io.helidon.http.HeaderNames.X_FORWARDED_FOR;

/// 功能注册
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class MahuFeature implements HttpFeature, Filter {

    private static final Splitter COMMA_SPLITTER = Splitter.on(',').omitEmptyStrings();
    private static final HeaderName X_REQUEST_ID = HeaderNames.create("x-request-id");

    private final List<HttpService> httpServices;

    public MahuFeature(List<HttpService> httpServices) {
        this.httpServices = httpServices;
    }

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.addFilter(this).addFeature(new MahuErrorFeature());

        // 注册 HTTP 服务
        for (HttpService httpService : httpServices) {
            routing.register(httpService);
        }

        routing.get("/openapi/ui", (req, res) -> {
            // 输出接口文档界面
            res.header(HeaderNames.CONTENT_TYPE, MediaTypes.TEXT_HTML.type())
                    .send(Resource.create("META-INF/openapi-ui.html").bytes());
        });
    }

    @Override
    public void filter(FilterChain chain, RoutingRequest req, RoutingResponse res) {
        // 设置请求访问元数据
        req.context().supply(Metadata.class, () -> new Metadata() {

            @Override
            public String clientAddr() {
                return req.headers()
                        .first(X_FORWARDED_FOR)
                        .flatMap(s -> {
                            var ipList = COMMA_SPLITTER.splitToList(s);
                            if (ipList.isEmpty()) {
                                return Optional.empty();
                            }
                            return Optional.of(ipList.getFirst());
                        })
                        .orElseGet(() -> req.remotePeer().host());
            }

            @Override
            public String traceId() {
                return req.headers().first(X_REQUEST_ID).orElseGet(() -> UlidCreator.getUlid()
                        .toLowerCase());
            }
        });

        chain.proceed();
    }
}
