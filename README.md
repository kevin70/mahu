# Mahu

## 开发准备

- Java 23
- IntelliJ IDEA
    - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
    - [JPA Buddy](https://plugins.jetbrains.com/plugin/15075-jpa-buddy)
    - [MapStruct Support](https://plugins.jetbrains.com/plugin/10036-mapstruct-support)
    - [String Manipulation](https://plugins.jetbrains.com/plugin/2162-string-manipulation)
    - [Palantir Java Format](https://plugins.jetbrains.com/plugin/13180-palantir-java-format)
    - [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)
    - [SonarQube for IDE](https://plugins.jetbrains.com/plugin/7973-sonarqube-for-ide)

### Palantir Java Format 配置

1. 去到 `File → Settings → Editor → Code Style`
2. 单击带有工具提示的扳手图标显示计划动作
3. 点击 `Import Scheme`
4. 选择项目根目录下 `config/idea/intellij-java-palantir-style.xml` 文件
5. 确保选择 **PalantirStyle** 作为当前方案

## Java Package 规范

* `util` 工具类
* `entity` 数据实体与数据库表一一对应
* `repository` 数据库访问对象（类名以 `Repository` 结尾）
* `service` 业务服务对象（类名以 `Service` 结尾）
* `shared` 共享内部公共业务逻辑
* `controller` REST 接口对象（类名以 `Controller` 结尾）

```
+----------+         +------------+
|  entity  |<--------| repository |<------,
+----------+         +------------+       |
                          ^               |
                          |         +----------+
                          |         |  shared  |
                          |         |  service |
                          |         +----------+
                          |              ^   |
                     +-----------+       |   |
                     |  service  |-------'   | Output
                     +-----------+           |
                          ^   |              V
                          |   | Output  +--------------+
                          |   +-------->|  dto/entity  |
                          |             +--------------+
+----------+  Input  +------------+           |
| request  |-------->| controller |<----------+
+----------+         +------------+
                          |
+----------+              |
| response |<-------------+
+----------+
```

## 构建文档

```
podman run --rm -it -v ./:/docs docker.1ms.run/kevin70/houge-mkdocs-material build
```

## 应用环境可选名称

| 简写   | 全称                         | 用途                     |
|------|----------------------------|------------------------|
| DEV  | Development Environment    | 开发环境，开发者本地或共享编码调试环境    |
| SIT  | System Integration Testing | 系统集成测试环境，验证多个模块/服务间的交互 |
| UAT  | User Acceptance Testing    | 用户验收测试环境，业务方验证功能是否符合需求 |
| STG  | Staging Environment        | 预发布环境，功能与PROD一致，用于最终验证 |
| PROD | Production Environment     | 生产环境，面向真实用户的线上环境       |
