# WebServer

## 官方入口

- `WebServer`: `https://helidon.io/docs/latest/apidocs/io.helidon.webserver/io/helidon/webserver/WebServer.html`
- `HttpFeature`: `https://helidon.io/docs/latest/apidocs/io.helidon.webserver/io/helidon/webserver/http/HttpFeature.html`

## 推荐模式

- 应用入口负责启动 `Main` / `WebServer`，不要把业务装配塞进 `main`。
- 需要一次性注册异常处理、全局过滤器、多个 `HttpService` 时，优先实现 `HttpFeature`。
- 单一路由簇或单一资源端点，优先 `HttpService`，不要为了“统一”再包一层空转发 Feature。
- 错误处理、认证、安全、请求上下文这类横切逻辑放在 `routing.error(...)`、`routing.security(...)` 或全局 Filter。
- Feature 顺序敏感时，用 `@Weight` 或 `Weighted` 明确顺序；不要依赖偶然的发现顺序。

## 适用场景

- 需要同时注册多个 Controller / `HttpService`
- 需要全局 Filter
- 需要全局异常映射
- 需要把 HTTP 装配与业务逻辑分离

## 不推荐写法

- 继续使用旧式 helper / bridge，只是为了包一下 `HttpRouting.Builder`
- 把业务查询、数据库写入、复杂授权判断直接塞进 Filter
- 把 `Observe`、`metrics`、健康检查端点混进业务路由树里
- 使用已经被官方标为待移除的 `HelidonFeatureSupport` 风格包装；新代码直接实现 `HttpFeature`

## 短例子

```java
@Service.Singleton
@Weight(Weighted.DEFAULT_WEIGHT + 5)
class AppHttpFeature implements HttpFeature {

    private final List<HttpService> services;
    private final List<Filter> filters;

    @Override
    public void setup(HttpRouting.Builder routing) {
        routing.error(Throwable.class, new RestErrorHandler());
        for (Filter filter : filters) {
            routing.addFilter(filter);
        }
        for (HttpService service : services) {
            routing.register(service);
        }
    }
}
```

## 验证点

- 路由注册职责是否集中在 Feature / Service，而不是散在多个 `main` 或工具类里
- 全局过滤器是否只做横切逻辑
- 错误响应是否统一出口
- Feature 顺序是否显式可读
