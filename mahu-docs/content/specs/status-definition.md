# 系统状态码定义及使用规范

## 概述

本文档定义了系统中各种状态的标准化编码和描述，采用数字编码体系便于状态管理和查询。

## 编码规则

| 范围 | 描述 | 编码范围 |
|------|------|----------|
| 1x | 初始/草稿/待处理状态 | 10-19 |
| 2x | 正向/已生效状态 | 20-29 |
| 3x | 支付/退款状态 | 30-39 |
| 4x | 物流状态 | 40-49 |
| 7x | 限定范围状态 | 70-79 |
| 8x | 终结/历史状态 | 80-99 |

## 状态定义

### 1x - 初始/草稿/待处理状态

| 状态码 | 状态名称 | 描述 | 使用场景 |
|--------|----------|------|----------|
| 10 | DRAFT | 草稿 | 临时未正式提交的内容 |
| 11 | PENDING | 待处理 | 等待人工或系统处理，如待审核、待确认 |
| 12 | SUSPENDED | 暂停 | 临时挂起，可恢复的状态 |
| 16 | PROCESSING | 进行中 | 处理中，如第三方回调、长时处理 |

### 2x - 正向/已生效状态

| 状态码 | 状态名称 | 描述 | 使用场景 |
|--------|----------|------|----------|
| 20 | APPROVED | 已批准 | 已通过审批流程 |
| 22 | ACTIVE | 活跃 | 处于生效状态，可正常使用 |

### 3x - 支付/退款状态

| 状态码 | 状态名称 | 描述 | 使用场景 |
|--------|----------|------|----------|
| 30 | PARTIAL_PAID | 部分支付 | 仅支付了部分金额 |
| 31 | PAID | 已支付 | 已完成全额支付 |
| 32 | PARTIAL_REFUNDED | 部分退款 | 仅退回了部分金额 |
| 33 | REFUNDED | 已退款 | 已完成全额退款 |

### 4x - 物流状态

| 状态码 | 状态名称 | 描述 | 使用场景 |
|--------|----------|------|----------|
| 40 | PACKAGED | 已打包 | 商品已打包完成，准备发货 |
| 41 | SHIPPED | 已发货 | 商品已发出，进入物流环节 |
| 42 | IN_TRANSIT | 运输中 | 商品在运输途中 |
| 43 | DELIVERED | 已送达 | 商品已送达目的地 |

### 7x - 限定范围状态

| 状态码 | 状态名称 | 描述 | 使用场景 |
|--------|----------|------|----------|
| 72 | LOCKED | 锁定 | 安全限制，如密码错误次数过多 |
| 74 | DISABLED | 已禁用 | 主动限制，如管理员手动禁用 |

### 8x - 终结/历史状态

| 状态码 | 状态名称 | 描述 | 使用场景 |
|--------|----------|------|----------|
| 86 | REJECTED | 被拒绝 | 被系统或人工拒绝 |
| 87 | VOIDED | 作废 | 作废无效 |
| 88 | COMPLETED | 已完成 | 流程或任务已完成 |
| 89 | FAILED | 失败 | 操作失败 |
| 90 | EXPIRED | 已过期 | 超过有效期 |
| 92 | INACTIVE | 非活跃 | 长期未使用 |
| 93 | CANCELLED | 已取消 | 用户主动取消 |
| 95 | DELETED | 已删除 | 逻辑删除 |
| 97 | ARCHIVED | 归档 | 归档存储 |
| 99 | TERMINATED | 已终止 | 永久终止 |

## 使用规范

### 1. 状态码使用原则

1. **唯一性**：每个状态码在整个系统中必须是唯一的
2. **不可变性**：状态码定义后不应随意修改
3. **语义明确**：状态名称和描述应清晰表达其含义
4. **范围合理**：状态码应按照编码规则分配到合适的范围

### 2. 状态流转规则

#### 2.1 正向流转（正常业务流程）
- DRAFT(10) → PENDING(11) → PROCESSING(16) → APPROVED(20) → ACTIVE(22)

#### 2.2 支付流程
- PENDING(11) → PARTIAL_PAID(30) → PAID(31)

#### 2.3 退款流程
- PAID(31) → PARTIAL_REFUNDED(32) → REFUNDED(33)

#### 2.4 终结流程
- ACTIVE(22) → CANCELLED(93) 或 DELETED(95) 或 ARCHIVED(97)

### 3. 状态查询方法

#### 3.1 根据状态码获取枚举实例
- 安全获取：返回 Optional 对象，状态码无效时返回空
- 强制获取：状态码无效时抛出 IllegalArgumentException 异常

#### 3.2 判断状态码是否有效
- 检查状态码是否在有效范围内

#### 3.3 状态比较
- 比较当前状态码与指定状态码是否相等
- 比较当前状态码与指定状态码是否不相等

### 4. 状态转换限制

1. **不可逆转换**：终结状态（8x）不应再转换为其他状态
2. **限定状态**：7x 状态需要特殊权限才能修改
3. **支付状态**：3x 状态应通过支付系统管理
4. **业务约束**：状态转换应符合业务逻辑

### 5. 最佳实践

1. **状态检查**：在状态转换前检查当前状态是否允许转换
2. **状态日志**：记录重要的状态变更历史
3. **状态验证**：验证状态码有效性
4. **状态映射**：避免空指针异常
5. **状态分组**：按范围查询相关状态，如查询所有 1x 状态

### 6. 错误处理

1. **无效状态码**：捕获无效状态码异常
2. **状态冲突**：当状态转换不符合业务规则时抛出业务异常
3. **状态回滚**：支持事务性状态变更，失败时回滚

## 使用案例

### 1. 用户状态设计

#### 1.1 用户生命周期状态流转

**用户注册流程：**
```
DRAFT(10) → PENDING(11) → APPROVED(20) → ACTIVE(22)
```

**用户状态管理：**
- **正常用户**：ACTIVE(22)
- **待审核用户**：PENDING(11)
- **暂停用户**：SUSPENDED(12)
- **锁定用户**：LOCKED(72) - 密码错误次数过多
- **禁用用户**：DISABLED(74) - 管理员手动禁用
- **非活跃用户**：INACTIVE(92) - 长期未登录
- **已删除用户**：DELETED(95) - 逻辑删除

#### 1.2 用户状态取值示例

```yaml
# 用户实体状态字段设计
user:
  status: 22  # ACTIVE
  status_history:
    - { timestamp: "2026-01-01T10:00:00", status: 10, reason: "用户注册" }
    - { timestamp: "2026-01-01T10:01:00", status: 11, reason: "提交审核" }
    - { timestamp: "2026-01-01T10:30:00", status: 20, reason: "审核通过" }
    - { timestamp: "2026-01-01T10:31:00", status: 22, reason: "激活成功" }
```

### 2. 订单状态设计

#### 2.1 订单生命周期状态流转

**正常订单流程（含物流）：**
```
订单状态：DRAFT(10) → PENDING(11) → PROCESSING(16) → PAID(31) → COMPLETED(88)
物流状态：PACKAGED(40) → SHIPPED(41) → IN_TRANSIT(42) → DELIVERED(43)
```

**退款订单流程：**
```
PAID(31) → PROCESSING(16) → PARTIAL_REFUNDED(32) → REFUNDED(33) → COMPLETED(88)
```

**取消订单流程：**
```
PENDING(11) → CANCELLED(93)
```

#### 2.2 订单状态取值示例

```yaml
# 订单实体状态字段设计
order:
  # 订单主状态
  status: 31  # PAID
  # 支付状态
  payment_status: 31  # PAID
  refund_status: null
  # 物流状态
  logistics_status: 41  # SHIPPED
  # 状态历史
  status_history:
    - { timestamp: "2026-01-01T09:00:00", status: 10, reason: "创建订单" }
    - { timestamp: "2026-01-01T09:01:00", status: 11, reason: "提交订单" }
    - { timestamp: "2026-01-01T09:05:00", status: 16, reason: "处理支付" }
    - { timestamp: "2026-01-01T09:10:00", status: 31, reason: "支付成功" }
    - { timestamp: "2026-01-01T14:00:00", status: 40, reason: "商品打包完成" }
    - { timestamp: "2026-01-01T15:30:00", status: 41, reason: "商品已发货" }
    - { timestamp: "2026-01-02T10:00:00", status: 42, reason: "商品运输中" }
    - { timestamp: "2026-01-03T14:00:00", status: 43, reason: "商品已送达" }
    - { timestamp: "2026-01-03T16:00:00", status: 88, reason: "订单完成" }
```

### 3. 内容审核状态设计

#### 3.1 内容审核流程

**审核通过流程：**
```
DRAFT(10) → PENDING(11) → PROCESSING(16) → APPROVED(20) → ACTIVE(22)
```

**审核拒绝流程：**
```
DRAFT(10) → PENDING(11) → PROCESSING(16) → REJECTED(86)
```

**内容下架流程：**
```
ACTIVE(22) → SUSPENDED(12)  # 临时下架
ACTIVE(22) → DELETED(95)    # 永久删除
```

#### 3.2 内容状态取值示例

```yaml
# 内容实体状态字段设计
content:
  status: 22  # ACTIVE
  audit_status: 20  # APPROVED
  publish_status: 22  # ACTIVE
  status_history:
    - { timestamp: "2026-01-01T08:00:00", status: 10, reason: "创建草稿" }
    - { timestamp: "2026-01-01T08:30:00", status: 11, reason: "提交审核" }
    - { timestamp: "2026-01-01T09:00:00", status: 16, reason: "审核中" }
    - { timestamp: "2026-01-01T09:30:00", status: 20, reason: "审核通过" }
    - { timestamp: "2026-01-01T10:00:00", status: 22, reason: "发布上线" }
```

### 4. 支付状态设计

#### 4.1 支付流程状态流转

**全额支付流程：**
```
PENDING(11) → PROCESSING(16) → PAID(31)
```

**部分支付流程：**
```
PENDING(11) → PROCESSING(16) → PARTIAL_PAID(30) → PAID(31)
```

**退款流程：**
```
PAID(31) → PROCESSING(16) → PARTIAL_REFUNDED(32) → REFUNDED(33)
```

#### 4.2 支付状态取值示例

```yaml
# 支付记录状态字段设计
payment:
  status: 31  # PAID
  amount: 100.00
  paid_amount: 100.00
  refund_amount: 0.00
  status_history:
    - { timestamp: "2026-01-01T11:00:00", status: 11, reason: "创建支付" }
    - { timestamp: "2026-01-01T11:01:00", status: 16, reason: "支付处理中" }
    - { timestamp: "2026-01-01T11:02:00", status: 31, reason: "支付成功" }
```

### 5. 状态查询与统计

#### 5.1 状态范围查询

```sql
-- 查询所有待处理的内容
SELECT * FROM content WHERE status BETWEEN 10 AND 19;

-- 查询所有活跃用户
SELECT * FROM user WHERE status = 22;

-- 查询所有终结状态的订单
SELECT * FROM order WHERE status BETWEEN 80 AND 99;

-- 统计各状态用户数量
SELECT status, COUNT(*) as count FROM user GROUP BY status ORDER BY status;
```

#### 5.2 状态转换验证

```python
# 状态转换验证逻辑示例
def can_transition(current_status, target_status):
    # 定义允许的状态转换规则
    allowed_transitions = {
        # 订单状态转换
        10: [11, 93],  # DRAFT -> PENDING, CANCELLED
        11: [16, 20, 93],  # PENDING -> PROCESSING, APPROVED, CANCELLED
        16: [20, 86, 89],  # PROCESSING -> APPROVED, REJECTED, FAILED
        20: [22, 93],  # APPROVED -> ACTIVE, CANCELLED
        22: [12, 72, 74, 92, 93, 95, 97, 99],  # ACTIVE -> 各种终结状态
        31: [32, 33],  # PAID -> PARTIAL_REFUNDED, REFUNDED
        
        # 物流状态转换
        40: [41, 93],  # PACKAGED -> SHIPPED, CANCELLED
        41: [42, 93],  # SHIPPED -> IN_TRANSIT, CANCELLED
        42: [43, 89],  # IN_TRANSIT -> DELIVERED, FAILED
        43: [88],      # DELIVERED -> COMPLETED
    }
    
    return target_status in allowed_transitions.get(current_status, [])

# 使用示例
if can_transition(current_user.status, 72):  # 检查是否可以锁定用户
    user.status = 72  # LOCKED
    user.save()

# 物流状态转换示例
if can_transition(order.logistics_status, 41):  # 检查是否可以发货
    order.logistics_status = 41  # SHIPPED
    order.save()
```

### 6. 状态监控与告警

#### 6.1 异常状态监控

```yaml
# 状态监控配置
monitoring:
  # 长时间处于处理中的订单
  long_processing:
    condition: "status = 16 AND updated_at < NOW() - INTERVAL '1 HOUR'"
    alert_level: "warning"
    action: "notify_operator"
  
  # 支付失败率过高
  payment_failure_rate:
    condition: "status = 89 AND created_at > NOW() - INTERVAL '1 HOUR'"
    threshold: 0.05  # 5%
    alert_level: "critical"
    action: "pause_payment_gateway"
  
  # 大量用户被锁定
  user_lock_spike:
    condition: "status = 72 AND created_at > NOW() - INTERVAL '10 MINUTES'"
    threshold: 100
    alert_level: "warning"
    action: "investigate_security"
  
  # 物流异常监控
  logistics_stuck:
    condition: "logistics_status = 41 AND updated_at < NOW() - INTERVAL '3 DAYS'"
    alert_level: "warning"
    action: "check_logistics"
  
  # 长时间未送达
  delivery_delayed:
    condition: "logistics_status = 42 AND updated_at < NOW() - INTERVAL '7 DAYS'"
    alert_level: "critical"
    action: "contact_logistics_provider"
```

## 扩展建议

1. **新增状态**：应在对应范围内选择未使用的编码
2. **状态分组**：可考虑按业务模块进一步分组
3. **国际化**：状态描述可支持多语言
4. **状态统计**：按范围统计各状态数量，便于监控

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2026-02-28 | 初始版本，基于当前 Status.java 定义 |

---

**文档维护**：状态定义变更时，应同步更新此文档
**负责人**：系统架构师
**最后更新**：2026-02-28