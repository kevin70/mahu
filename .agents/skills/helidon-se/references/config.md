# Config

## 官方入口

- `Config`: `https://helidon.io/docs/latest/apidocs/io.helidon.config/io/helidon/config/Config.html`

## 推荐模式

- 简单场景用 `Config.create()` 或通过注入拿到 `Config` 根节点。
- 读取配置时先下钻到稳定的子树，再做类型映射；不要在各个类里散落大量重复 key。
- 一个配置块只要跨多个类共享、或者字段超过 2 到 3 个，就优先整理成 typed config / provider，而不是到处 `config.get("...")`。
- 时间间隔、超时、开关、阈值等运行参数优先配置化，但代码里仍保留安全默认值。
- 只在装配层处理原始 key 字符串；业务层优先依赖已经完成映射的配置对象。

## 适用场景

- 客户端超时
- 调度间隔
- 观测端点开关
- fault-tolerance 参数
- 共享的业务配置对象

## 不推荐写法

- 同一个 key 在多个类里硬编码
- 把 `Config` 作为全局字典到处传递
- 业务逻辑里直接拼配置路径
- 把配置解析和业务决策混在一个长方法里

## 短例子

```java
@Service.Singleton
class TokenConfigProvider implements Supplier<TokenConfig> {

    private final TokenConfig value;

    TokenConfigProvider(Config root) {
        this.value = TokenConfig.create(root.get("token"));
    }

    @Override
    public TokenConfig get() {
        return value;
    }
}
```

## 验证点

- 是否先把配置收敛到子树或 typed config
- 是否保留了默认值和缺省行为
- 是否避免了业务层重复出现同一组配置 key
