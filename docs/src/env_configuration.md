# 环境配置

## 环境定义

| 简写   | 全称                         | 用途                       |
|------|----------------------------|--------------------------|
| DEV  | Development Environment    | 开发环境，开发者本地或共享编码调试环境      |
| SIT  | System Integration Testing | 系统集成测试环境，验证多个模块/服务间的交互   |
| UAT  | User Acceptance Testing    | 用户验收测试环境，业务方验证功能是否符合需求   |
| STG  | Staging Environment        | 预发布环境，功能与 PROD 一致，用于最终验证 |
| PROD | Production Environment     | 生产环境，面向真实用户的线上环境         |

## 认证客户端

| client_id       | 环境                | 描述                |
|-----------------|-------------------|-------------------|
| `0KPY6J2C5XP5R` | `DEV`,`SIT`,`UAT` | 开发测试环境可使用的认证终端 ID |

## 支付通道

| payment_channel_id | 描述        |
|--------------------|-----------|
| `0M8ZMZEPVX5AE`    | 微信小程序支付通道 |
