# Mahu

## 开发准备

- Java 23
- IntelliJ IDEA
    - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
    - [JPA Buddy](https://plugins.jetbrains.com/plugin/15075-jpa-buddy)
    - [MapStruct Support](https://plugins.jetbrains.com/plugin/10036-mapstruct-support)
    - [String Manipulation](https://plugins.jetbrains.com/plugin/2162-string-manipulation)
    - [Palantir Java Format](https://plugins.jetbrains.com/plugin/13180-palantir-java-format)
    - [SonarQube for IDE](https://plugins.jetbrains.com/plugin/7973-sonarqube-for-ide)

### Google Java Format 配置

1. 去到 `File → Settings → Editor → Code Style`
2. 单击带有工具提示的扳手图标显示计划动作
3. 点击 `Import Scheme`
4. 选择项目根目录下 `dev/intellij-java-google-style.xml` 文件
5. 确保选择 GoogleStyle 作为当前方案

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
## 构建文档

```
podman run --rm -it -v ./:/docs docker.1ms.run/kevin70/houge-mkdocs-material build
```

