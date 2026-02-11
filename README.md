# ObsidianVariable (CustomVariable)

![ObsidianVariable Icon](assets/obsidian-plugin-series-icon.png)

一个给 Bukkit/Spigot 服务器用的**类型化变量插件**。  
你可以把它理解成“可注册、可校验、可同步”的变量中心：

- 面向全服的公共变量（GLOBAL）
- 面向玩家的独立变量（PLAYER）
- Placeholder 调用
- 第三方插件 API 调用

仓库地址：<https://github.com/EasyCode-Obsidian/ObsidianVariable>

## 许可与商用声明

本项目采用 **PolyForm Noncommercial 1.0.0**（见 `LICENSE`）。

这代表：

- 你可以学习、修改、二次开发、分发源码
- **禁止任何商业用途**

如需商业授权，请先联系作者沟通授权方案。

## 交流与反馈

- QQ 交流群：`996224266`
- 问题反馈：GitHub Issues

## 插件能力

- 双作用域变量体系：`GLOBAL`（全服）与 `PLAYER`（玩家）
- 强类型注册：变量必须先注册再读写，避免“随手写错键名”
- 支持 10 种类型：`string`、`int`、`long`、`double`、`decimal`、`boolean`、`uuid`、`text`、`json`、`list_string`
- 默认值体系：注册可写默认值；未显式写值时自动回退默认值
- Placeholder 支持：全服变量、当前玩家变量、指定玩家变量
- 存储支持：`SQLite / MySQL` 二选一
- 缓存与同步：内存先写、异步落库、定时 flush + 增量 pull
- 命令管理：注册项、全局变量、玩家变量、同步、重载、数据库状态
- API 开放：提供稳定门面 `CustomVariableApi` 给其他插件直接调用
- 事件通知：变量注册、删除、类型变更、值变更事件
- 多语言：内置 `zh_CN` / `en_US`

## 快速开始

1. 把插件放进 `plugins/` 并启动一次服务器。
2. 修改 `plugins/CustomVariable/config.yml`（数据库类型、连接信息等）。
3. 重启服务器或执行 `/cv reload`。

默认是 SQLite，无需额外数据库服务即可跑起来。

## 命令速查

主命令：`/cv`

- `registry`
  - `/cv registry list [scope]`
  - `/cv registry add <scope> <key> <type> [default]`
  - `/cv registry del <scope> <key>`
  - `/cv registry settype <scope> <key> <type>`
- `global`
  - `/cv global list`
  - `/cv global get <key>`
  - `/cv global set <key> <value>`
  - `/cv global del <key>`
- `player`
  - `/cv player list <player|uuid>`
  - `/cv player get <player|uuid> <key>`
  - `/cv player set <player|uuid> <key> <value>`
  - `/cv player del <player|uuid> <key>`
- 其他
  - `/cv sync`
  - `/cv db status`
  - `/cv reload`

## Placeholder 用法

标识符：`cv`

- `%cv_global:<key>%`
- `%cv_player:<key>%`
- `%cv_player_of:<uuidOrName>:<key>%`

示例：

- `%cv_global:server_state%`
- `%cv_player:coins%`
- `%cv_player_of:Notch:rank%`

## 变量键与类型说明

- 变量键规则：`^[a-zA-Z0-9_.:-]{1,64}$`
- 作用域：
  - `global`：全服共享一份值
  - `player`：每个玩家独立一份值

## API 接口文档

开发者文档见：`docs/API.md`

API 入口：

- `ink.easycode.customvariable.api.CustomVariableApi`

## 构建

```bash
./gradlew clean build
```

Windows:

```powershell
.\gradlew.bat clean build
```
