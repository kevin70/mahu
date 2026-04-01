# Mahu

## 相关项目

- [mahu-dashboard（后台仪表盘）](https://gitee.com/kk70/mahu-dashboard)

## 开发准备

- [Java 25](https://adoptium.net/zh-CN)
- [IntelliJ IDEA](https://www.jetbrains.com/zh-cn/idea/)
    - [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok)
    - [JPA Buddy](https://plugins.jetbrains.com/plugin/15075-jpa-buddy)
    - [MapStruct Support](https://plugins.jetbrains.com/plugin/10036-mapstruct-support)
    - [String Manipulation](https://plugins.jetbrains.com/plugin/2162-string-manipulation)
    - [Palantir Java Format](https://plugins.jetbrains.com/plugin/13180-palantir-java-format)
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
* `shared` 共享内部公共业务逻辑（类名以 `SharedService` 结尾）
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
+----------+  Output      |
| response |<-------------+
+----------+
```

## OpenAPI 与文档常用命令

在项目根目录执行：

- **安装 OpenAPI 相关前端依赖**：

  ```bash
  ./gradlew openapiDepsInstall
  ```

- **生成/更新管理端 OpenAPI 服务器端代码**（包括先打包 OpenAPI，再生成 Helidon SE 代码并复制到 `mahu-admin-web/src/main/gen`）：

  ```bash
  ./gradlew :mahu-admin:mahu-admin-web:openApiGenerate
  ```

- **构建文档站点**（如需）：

  ```bash
  cd docs && pnpm build
  ```

## 构建/测试/格式化常用命令

在项目根目录执行：

- 构建：`./gradlew build`
- 测试：`./gradlew test`
- 单模块测试示例：`./gradlew :mahu-web:test`
- 格式化自检：`./gradlew spotlessCheck`
- 应用格式化：`./gradlew spotlessApply`

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
docker run -d --name postgres -p 15432:5432 -e POSTGRES_DB=mahu-dev -e POSTGRES_USER=test -e POSTGRES_PASSWORD=test postgres:17.5-alpine postgres -c fsync=off
```

### 启动 MinIO

```
docker run -d --name minio -p 19000:9000 -p 19001:9001 -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin minio/minio:RELEASE.2025-05-24T17-08-30Z server --console-address :9001 /data
```

## 申请 HTTPS 证书

```
docker run --rm -it -v certs_volume:/acme.sh -e Ali_Key=$Ali_Key -e Ali_Secret=$Ali_Secret\
 neilpang/acme.sh --issue --server zerossl\
 --eab-kid $EAB_KID --eab-hmac-key $EAB_HMAC_KEY --dns dns_ali -d $DOMAIN -d *.$DOMAIN
```

- `Ali_Key` 阿里云 API 凭证
- `Ali_Secret` 阿里去 API 凭证
- `EAB_KID` ZeroSSL API 凭证
- `EAB_HMAC_KEY` ZeroSSL API 凭证
- `DOMAIN` 申请证书的域名

### 获取阿里云 DNS API 凭证

1. 登录[阿里云控制台](https://ram.console.aliyun.com/) → RAM 访问控制 → 用户管理。
2. 创建用户（或使用现有用户），勾选`OpenAPI`调用访问。
3. 为该用户添加权限`AliyunDNSFullAccess`（DNS 完全管理权限）。
4. 在 安全信息 选项卡中，创建 `AccessKey`（保存`AccessKey ID`和`AccessKey Secret`）。

### 获取 ZeroSSL API 凭证

1. 注册 [ZeroSSL](https://zerossl.com/) 账户
2. 登录后，进入 Developer 页面：
    - 直接访问 https://app.zerossl.com/developer
    - 或通过顶部菜单栏：`Dashboard`→`Developer`
3. 在 API Keys 区域，点击 Generate 创建新 API Key
4. 复制生成的 Key（如 xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx）
