# CustomVariable API 文档

本文面向二次开发者，介绍如何在其他插件里调用 CustomVariable。

## 1. 基本信息

- API 包名：`ink.easycode.customvariable.api`
- 当前 API 版本：`1.0.0`
- 入口对象：`CustomVariableApi`

```kotlin
import ink.easycode.customvariable.api.CustomVariableApi

val ready = CustomVariableApi.isReady()
val apiVersion = CustomVariableApi.apiVersion()
```

> 建议先判断 `isReady()`，未就绪时接口会返回 `NOT_READY`。

## 1.1 接入方式

本项目当前未发布到公共 Maven 仓库，常见做法是把插件 JAR 作为 `compileOnly` 引入：

```kotlin
dependencies {
    compileOnly(files("libs/CustomVariable.jar"))
}
```

仅作为编译依赖即可，不要把它二次打包进你的插件。

## 2. 数据模型

### 2.1 作用域（ApiVariableScope）

- `GLOBAL`：全服共享变量
- `PLAYER`：按玩家 UUID 隔离变量

### 2.2 类型（ApiVariableType）

- `STRING`
- `INT`
- `LONG`
- `DOUBLE`
- `DECIMAL`
- `BOOLEAN`
- `UUID`
- `TEXT`
- `JSON`
- `LIST_STRING`

### 2.3 返回体（ApiResult<T>）

所有 API 都返回 `ApiResult<T>`：

- `ApiResult.Ok(data)`：成功
- `ApiResult.Error(code, message, args)`：失败

常见用法：

```kotlin
import ink.easycode.customvariable.api.ApiResult

when (val result = CustomVariableApi.access().getGlobal("motd")) {
    is ApiResult.Ok -> logger.info("motd=${result.data}")
    is ApiResult.Error -> logger.warning("error=${result.code}, args=${result.args}")
}
```

## 3. API 入口与接口

### 3.1 注册表接口（VariableRegistryApi）

由 `CustomVariableApi.registry()` 获取。

- `register(scope, key, type, defaultRaw, description)`
  - 注册变量定义
- `unregister(scope, key)`
  - 删除注册定义
- `changeType(scope, key, newType)`
  - 变更类型（会重置该键对应值）
- `find(scope, key)`
  - 查询单个注册项
- `list(scope?)`
  - 查询注册项列表（可按作用域过滤）

### 3.2 读写接口（VariableAccessApi）

由 `CustomVariableApi.access()` 获取。

- `setGlobal(key, rawValue)`
- `getGlobal(key)`
- `deleteGlobal(key)`
- `setPlayer(playerUuid, key, rawValue)`
- `getPlayer(playerUuid, key)`
- `deletePlayer(playerUuid, key)`

### 3.3 查询接口（VariableQueryApi）

由 `CustomVariableApi.query()` 获取。

- `listGlobalValues()`
- `listPlayerValues(playerUuid)`
- `batchGetGlobal(keys)`
- `batchGetPlayer(playerUuid, keys)`

### 3.4 同步接口（VariableSyncApi）

由 `CustomVariableApi.sync()` 获取。

- `flushNow()`：立即执行一次同步周期
- `pullNow()`：立即执行一次同步周期
- `status()`：读取同步状态快照

> 当前实现中 `flushNow()` 与 `pullNow()` 都会触发同一套立即同步流程。

## 4. 错误码（ApiErrorCode）

- `NOT_READY`：插件未完成启动或正在重载
- `VARIABLE_NOT_REGISTERED`：变量未注册
- `VARIABLE_ALREADY_REGISTERED`：变量已存在
- `INVALID_KEY`：变量键不合法
- `INVALID_TYPE_VALUE`：值与目标类型不匹配
- `REGISTRY_DISABLED`：变量注册项被禁用
- `PLAYER_NOT_FOUND`：玩家标识无效
- `DATABASE_ERROR`：数据库层异常
- `INTERNAL_ERROR`：其他内部错误

## 5. 事件接口

可监听以下 Bukkit 代理事件（`taboolib.platform.type.BukkitProxyEvent`）：

- `VariableRegisteredEvent`
  - 字段：`entry: ApiRegistryEntry`
- `VariableTypeChangedEvent`
  - 字段：`scope`、`key`、`oldType`、`newType`、`resetValueRaw`
- `VariableValueChangedEvent`
  - 字段：`value: ApiVariableValue`
- `VariableDeletedEvent`
  - 字段：`scope`、`ownerId`、`key`

## 6. Kotlin 示例

```kotlin
import ink.easycode.customvariable.api.ApiResult
import ink.easycode.customvariable.api.ApiVariableScope
import ink.easycode.customvariable.api.ApiVariableType
import ink.easycode.customvariable.api.CustomVariableApi

fun ensureCoins() {
    if (!CustomVariableApi.isReady()) return

    val registryApi = CustomVariableApi.registry()
    val accessApi = CustomVariableApi.access()

    registryApi.register(
        scope = ApiVariableScope.PLAYER,
        key = "coins",
        type = ApiVariableType.INT,
        defaultRaw = "0",
        description = "玩家金币"
    )

    when (val result = accessApi.setPlayer("00000000-0000-0000-0000-000000000000", "coins", "100")) {
        is ApiResult.Ok -> {
            // ok
        }
        is ApiResult.Error -> {
            // handle error
        }
    }
}
```

## 7. Java 示例

```java
import ink.easycode.customvariable.api.*;

public final class Example {

    public void readGlobal() {
        if (!CustomVariableApi.INSTANCE.isReady()) {
            return;
        }

        ApiResult<String> result = CustomVariableApi.INSTANCE.access().getGlobal("server_state");
        if (result instanceof ApiResult.Ok) {
            String value = ((ApiResult.Ok<String>) result).getData();
            System.out.println("server_state=" + value);
            return;
        }

        ApiResult.Error error = (ApiResult.Error) result;
        System.out.println("error=" + error.getCode());
    }
}
```

## 8. 性能与线程建议

- API 本身没有强制线程限制，但涉及玩家首次读取时可能触发缓存预热。
- 批量读写、离线玩家批处理建议放到异步线程执行。
- 不要在高频 Tick 逻辑里做大规模 `batchGet` 或全量 `list`。

