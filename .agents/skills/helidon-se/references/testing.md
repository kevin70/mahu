# Testing

## 官方入口

- `io.helidon.testing`: `https://helidon.io/docs/latest/apidocs/io.helidon.testing/io/helidon/testing/package-summary.html`
- `TestJunitExtension`: `https://helidon.io/docs/latest/apidocs/io.helidon.testing.junit5/io/helidon/testing/junit5/TestJunitExtension.html`
- `Services`: `https://helidon.io/docs/latest/apidocs/io.helidon.service.registry/io/helidon/service/registry/Services.html`
- `io.helidon.webserver.testing.junit5`: `https://helidon.io/docs/latest/apidocs/io.helidon.webserver.testing.junit5/io/helidon/webserver/testing/junit5/package-summary.html`

## 推荐模式

- 纯服务逻辑、事件处理器、Provider、worker：
  - 优先 `@Testing.Test(perMethod = true)`
  - 通过 `Services.set(...)` 提前绑定 mock 或 stub
  - 让 Helidon 注入被测对象
- Web 层：
  - 优先 `helidon-webserver-testing-junit5`
  - 重点验证路由、过滤器、异常映射、序列化边界
- 定时任务：
  - 优先直接测 `executeAt(Instant)`，不要依赖真实调度线程等待
- metrics：
  - 验证是否记录了预期指标，而不是只测某个实现细节
- fault-tolerance：
  - 验证重试次数、超时、熔断状态和 fallback 行为

## 关键约束

`Services.set(...)` 必须在该 contract 第一次被解析之前调用。官方 `Services` 文档明确写了这一点。

## 不推荐写法

- 复杂依赖全手工 `new`
- 为了测业务规则而等待真实调度触发
- Web 层测试只断言 200，不检查错误映射和过滤器行为
- fault-tolerance 只测“最终成功”，不测重试和降级路径

## 短例子

```java
@Testing.Test(perMethod = true)
class FooServiceTest {

    private final BarClient barClient = mock(BarClient.class);

    @BeforeEach
    void setUp() {
        Services.set(BarClient.class, barClient);
    }

    @Test
    void load_works(FooService service) {
        when(barClient.load()).thenReturn("ok");
        assertEquals("ok", service.load());
    }
}
```

## 验证点

- mock 是否在首次解析前通过 `Services.set(...)` 绑定
- 测试是否覆盖成功路径和失败路径
- 后台任务是否直接验证可控执行入口
- Web 层是否验证了过滤器、异常处理和响应映射
