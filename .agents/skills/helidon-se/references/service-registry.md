# Service Registry

## 官方入口

- `Service`: `https://helidon.io/docs/latest/apidocs/io.helidon.service.registry/io/helidon/service/registry/Service.html`
- `Service.Singleton`: `https://helidon.io/docs/latest/apidocs/io.helidon.service.registry/io/helidon/service/registry/Service.Singleton.html`
- `Services`: `https://helidon.io/docs/latest/apidocs/io.helidon.service.registry/io/helidon/service/registry/Services.html`

## 推荐模式

- 生产代码优先构造器注入和 `final` 字段。
- 共享资源、客户端、缓存、数据库连接包装、观察能力等，优先 `@Service.Singleton`。
- 需要控制真实资源生命周期时，优先 `Supplier<T>` Provider，并在 `@Service.PreDestroy` 关闭资源。
- 初始化逻辑放在 `@Service.PostConstruct`；只在确实需要启动顺序时使用 `@Service.RunLevel`。
- `Services.set(...)` 主要用于测试或显式 late binding，不要把 `Services.get(...)` 当作生产代码里的默认依赖获取方式。

`Services` 的官方文档明确说明：使用它的静态访问会丢掉 Service Registry 插件生成的优化，因此生产代码优先显式注入。

## 作用域选择

- `@Service.Singleton`
  - 默认首选
  - 适合共享依赖、昂贵对象、后台 worker、Feature、Provider
- `@Service.PerLookup`
  - 只在每次解析都需要全新实例时使用
  - 适合极轻量、无共享状态、没有资源生命周期的对象
- `@Service.PerRequest`
  - 只在 Web 请求范围内有意义时使用
  - 不要滥用到普通业务服务

## 不推荐写法

- 普通业务代码依赖 `Services.get(...)`
- 为了“看起来统一”给简单工具类强行加 Service 注解
- 把资源初始化、调度注册、缓存预热塞进构造器
- 引入只做一层转发的 Provider / Service 包装

## 短例子

```java
@Service.Singleton
class WebClientProvider implements Supplier<WebClient> {

    private final WebClient client;

    WebClientProvider(ObjectMapper objectMapper) {
        this.client = WebClient.builder()
                .connectTimeout(Duration.ofSeconds(15))
                .addMediaSupport(JacksonSupport.create(objectMapper))
                .build();
    }

    @Override
    public WebClient get() {
        return client;
    }

    @Service.PreDestroy
    void destroy() {
        client.closeResource();
    }
}
```

## 验证点

- 依赖是否通过构造器注入
- 生命周期逻辑是否进入 `PostConstruct` / `PreDestroy`
- 是否只有真正需要时才使用 `RunLevel`
- 测试里是否在首次解析前调用 `Services.set(...)`
