# panda-common-plugin

一个基于 Java 的**代码生成框架核心模块**，用于根据数据库表结构与上下文配置，自动生成多层架构代码（DAL / Core / Facade 等）。

---

## 📌 项目定位

`panda-common-plugin` 本质是一个：

> ✅ **面向分层架构的代码生成引擎（Code Generator Framework）**

适用于：

* 标准多模块 Java 项目（DAL / Service / Facade 分层）
* MyBatis / MyBatis-Plus 项目
* 需要快速生成 CRUD 模板代码的场景

---

## 🧠 核心设计思想

该项目采用典型的：

### 👉 模板驱动 + 上下文驱动

| 组件              | 作用           |
| --------------- | ------------ |
| GenerateContext | 全局生成上下文      |
| Template        | 代码模板（生成规则）   |
| Definition      | 元数据定义（字段/项目） |

---

## 📦 核心模块说明

---

### 1️⃣ 字段定义：FieldDefinition

```java
public class FieldDefinition {
    private String name;        // Java字段名
    private String columnName;  // 数据库字段名
    private String type;        // Java类型
    private String comment;     // 字段注释
    private boolean primaryKey; // 是否主键
}
```

📌 功能：

* 描述数据库字段 → Java字段映射关系
* 用于模板渲染（DO / DTO / VO）

👉 示例：

| DB字段        | Java字段   |
| ----------- | -------- |
| user_name   | userName |
| create_time | Date     |

---

### 2️⃣ 项目定义：ProjectDefinition

```java
public class ProjectDefinition {
    private String projectRoot;

    private String projectName;

    private String dalModuleName;
    private String coreModelModuleName;
    private String facadeModuleName;
    private String coreServiceModuleName;
}
```

📌 功能：

* 描述**多模块项目结构**
* 控制代码生成输出位置

👉 支持典型分层：

```
project
├── dal
├── core-model
├── core-service
└── facade
```

---

### 3️⃣ 核心模板抽象：AbstractGenerateTemplate

这是整个系统最核心的类 👇

---

#### ✔ 3.1 上下文注入机制

```java
protected GenerateContext context;
```

```java
public void injectContext(GenerateContext context)
```

📌 说明：

* 所有模板运行时依赖 `GenerateContext`
* 避免参数传递污染

---

#### ✔ 3.2 通用数据模型构建

```java
protected Map<String, Object> buildBaseDataModel(GenerateContext context)
```

生成模板数据：

| Key            | 说明   |
| -------------- | ---- |
| className      | 类名   |
| tableName      | 表名   |
| tableDesc      | 表描述  |
| author         | 作者   |
| createDate     | 创建时间 |
| fields         | 字段列表 |
| primaryKeyName | 主键   |

---

#### ✔ 包结构自动生成

```java
dataModel.put("doPackage", basePackage + "common.dal.model");
dataModel.put("mapperPackage", basePackage + "common.dal.mapper");
dataModel.put("servicePackage", basePackage + "core.service");
```

👉 自动生成：

* DO
* Mapper
* Service
* Facade
* Convert

---

#### ✔ 技术选型开关

```java
dataModel.put("useLombok", context.isUseLombok());
dataModel.put("useMyBatisPlus", context.isUseMyBatisPlus());
```

📌 支持：

* Lombok
* MyBatis Plus

---

#### ✔ 文件路径生成（核心能力）

```java
public String getOutputPath(GenerateContext ctx, IGenerateTemplate template)
```

📌 输出路径规则：

```
{projectRoot}/{moduleName}/src/main/java/{package}/{ClassName}.java
```

或：

```
resources/{package}/{file}
```

👉 自动处理：

* module 分发
* 包路径转换
* 前缀/后缀拼接

---

#### ✔ 生成日志

```java
✓ 已生成: [Template] -> FileName
✗ 模板生成失败
```

---

## ⚙️ 代码生成流程

完整流程如下：

```
1️⃣ 构建 GenerateContext
   ↓
2️⃣ 定义 ProjectDefinition（模块结构）
   ↓
3️⃣ 定义 FieldDefinition（字段信息）
   ↓
4️⃣ 选择 Template（生成规则）
   ↓
5️⃣ buildDataModel()
   ↓
6️⃣ 渲染模板
   ↓
7️⃣ 输出文件（自动路径）
```

---

## 🧩 支持的代码层级

根据源码推断，默认支持：

* DO（数据库实体）
* Mapper
* Service
* Facade
* Convert（对象转换）

---

## 🚀 项目特点

### ✅ 优点

* 模板抽象清晰
* 支持多模块项目
* 自动路径生成（非常实用）
* 支持技术开关（Lombok / MyBatis Plus）
* 可扩展模板体系

---

## 🔥 适用场景

✔ 后端 CRUD 代码生成
✔ 多模块项目初始化
✔ 快速生成 DAL / Service 层
✔ 内部开发工具

---

## 🤝 贡献

欢迎扩展：

* 新模板（Controller / DTO）
* 多数据库支持
* 代码风格规范

---

## 📄 License

MIT License
