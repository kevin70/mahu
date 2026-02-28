
## 概述

public 模式包含公共表，主要用于字典管理等通用功能。该模式下的表可以被所有用户访问，包含系统级的配置数据。

## 表结构设计
### dict_type（字典类型表）
| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| id | VARCHAR(50) | PRIMARY KEY | 字典类型 ID |
| name | VARCHAR(255) | | 字典类型名称 |
| description | VARCHAR(4096) | | 描述 |
| enabled | BOOLEAN | | 是否启用 |
| visibility | SMALLINT | | 可见性（0：私有，仅限内部使用；1：公共的；2：受限的） |
| value_regex | VARCHAR(512) | | 字典值格式校验正则，由应用层在写入时执行校验 |
| created_at | TIMESTAMP | | 创建时间 |
| updated_at | TIMESTAMP | | 更新时间 |

**字段说明**：
- `visibility`：控制字典类型的可见性级别
  - 0：私有，仅限内部使用
  - 1：公共的，所有用户可见
  - 2：受限的，特定用户组可见
- `value_regex`：用于验证字典值的正则表达式，确保数据格式一致性

### dict（字典数据表）
| 字段名 | 数据类型 | 约束 | 说明 |
|--------|----------|------|------|
| dc | serial | PRIMARY KEY | 字典代码，自增序列 |
| label | VARCHAR(255) | | 字典数据文本 |
| value | VARCHAR(4096) | | 字典数据值 |
| enabled | BOOLEAN | | 是否启用 |
| ordering | INTEGER | | 排序值 |
| type_id | VARCHAR(50) | NOT NULL | 字典类型代码 |
| created_at | TIMESTAMP | | 创建时间 |
| updated_at | TIMESTAMP | | 更新时间 |

**索引**：
- `dict_type_id_ui`：在 type_id 字段上创建唯一索引

**序列管理**：
- 使用序列 `public.dict_dc_seq`，从100001开始

**字段说明**：
- `dc`：字典代码，作为主键的自增序列
- `label`：字典项的显示文本
- `value`：字典项的实际值，支持较长的字符串（最大4096字符）
- `ordering`：用于控制字典项显示顺序的排序值
- `type_id`：外键，关联到 `dict_type` 表的 `id` 字段

## 表关系设计

### 字典类型与字典数据关系
- `dict_type` 表与 `dict` 表是一对多关系
- 一个字典类型可以包含多个字典数据项
- `dict.type_id` 字段引用 `dict_type.id` 字段
- 通过 `dict_type_id_ui` 唯一索引确保每个字典类型在 `dict` 表中只有一条记录（根据业务需求）

### 数据完整性约束
1. **外键约束**：`dict.type_id` 必须引用 `dict_type.id` 中存在的值
2. **唯一性约束**：`dict_type.id` 是主键，确保字典类型ID唯一
3. **格式验证**：`dict_type.value_regex` 字段定义了字典值的格式要求

### 动态选项
- 为前端提供动态的下拉选项数据
- 支持多语言、多租户等场景

### 数据验证
- 通过 `value_regex` 字段确保字典值符合特定格式要求
- 支持电话号码、邮箱、身份证号等格式验证

## 维护建议

### 字典类型管理
- 定期审查字典类型的可见性设置，确保安全性
- 维护 `value_regex` 字段，确保格式验证规则正确

### 字典数据维护
- 定期清理不再使用的字典项（将 `enabled` 设置为 FALSE）
- 维护 `ordering` 字段，确保字典项显示顺序符合业务需求

### 性能监控
- 监控 `dict` 表的增长情况，考虑分区或归档策略
- 定期分析索引使用情况，优化查询性能

### 数据备份
- 字典数据通常包含重要的配置信息，建议定期备份
- 考虑版本控制，记录字典数据的变更历史