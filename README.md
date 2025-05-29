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

## 应用环境名称规范

| 简写   | 全称                         | 用途                     |
|------|----------------------------|------------------------|
| DEV  | Development Environment    | 开发环境，开发者本地或共享编码调试环境    |
| SIT  | System Integration Testing | 系统集成测试环境，验证多个模块/服务间的交互 |
| UAT  | User Acceptance Testing    | 用户验收测试环境，业务方验证功能是否符合需求 |
| STG  | Staging Environment        | 预发布环境，功能与PROD一致，用于最终验证 |
| PROD | Production Environment     | 生产环境，面向真实用户的线上环境       |

## 开发环境搭建

### 启动 PostgreSQL

```
podman run -d --name postgres -p 15432:5432 -e POSTGRES_DB=mahu-dev -e POSTGRES_USER=test -e POSTGRES_PASSWORD=test m.daocloud.io/docker.io/postgres:17.5-alpine postgres -c fsync=off
```

### 启动 RabbitMQ

```
podman run -d --name rabbitmq -p 15672:5672 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest m.daocloud.io/docker.io/rabbitmq:4.1.0-management-alpine
```

### 启动 MinIO

```
podman run -d --name minio -p 19000:9000 -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin m.daocloud.io/docker.io/minio/minio:RELEASE.2025-05-24T17-08-30Z server --console-address :9001 /data
```
