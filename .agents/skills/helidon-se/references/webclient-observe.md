# WebClient And Observe

## 官方入口

- `WebClient`: `https://helidon.io/docs/latest/apidocs/io.helidon.webclient.api/io/helidon/webclient/api/WebClient.html`
- `JacksonSupport`: `https://helidon.io/docs/latest/apidocs/io.helidon.http.media.jackson/io/helidon/http/media/jackson/JacksonSupport.html`
- `ObserveFeature`: `https://helidon.io/docs/latest/apidocs/io.helidon.webserver.observe/io/helidon/webserver/observe/ObserveFeature.html`
- `MetricsObserver`: `https://helidon.io/docs/latest/apidocs/io.helidon.webserver.observe.metrics/io/helidon/webserver/observe/metrics/MetricsObserver.html`

## WebClient 推荐模式

- 通过 `WebClient.builder()` 构建客户端。
- 在 Provider 中统一配置超时、协议偏好和 media support。
- JSON 场景优先显式加 `JacksonSupport.create(objectMapper)`，不要假设序列化支持会自动存在。
- WebClient 是资源对象，交给 singleton provider 管理，并在 `@Service.PreDestroy` 关闭。

## Observe 推荐模式

- 观测能力优先通过 `ObserveFeature` 暴露，不自己拼 `/health`、`/metrics` 等端点。
- 需要显式控制 observer 列表时，优先 `ObserveFeature.just(...)`。
- 只有在你明确接受 classpath 自动发现时，才用 `ObserveFeature.create()`。
- 同一个 observer 类型如果需要多个实例，统一用程序化方式配置，不要一半依赖发现、一半手工注册。

## 不推荐写法

- 在业务代码里随手 `WebClient.create()`，没有统一超时和 media support
- 不关闭 WebClient
- 把观测端点注册到业务 Feature 或 Controller
- 同时使用 classpath 自动发现和手工注册，导致 observer 重复或行为不清晰

## 短例子

```java
@Service.Singleton
class ObserveFeatureProvider implements Supplier<ObserveFeature> {
    @Override
    public ObserveFeature get() {
        return ObserveFeature.just(
                HealthObserver.create(),
                ConfigObserver.create(),
                LogObserver.create(),
                MetricsObserver.create());
    }
}
```

## 验证点

- WebClient 配置是否集中
- JSON 序列化支持是否显式
- Observe 是否通过专门 Feature 暴露
- observer 列表是否可读、可控
