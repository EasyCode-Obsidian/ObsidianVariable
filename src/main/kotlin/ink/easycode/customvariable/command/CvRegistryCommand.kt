package ink.easycode.customvariable.command

import ink.easycode.customvariable.cache.CacheManager
import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.model.VariableScope
import ink.easycode.customvariable.service.RegistryService
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponent
import top.maplex.arim.tools.commandhelper.createTabooLegacyStyleCommandHelper

object CvRegistryCommand {

    fun bind(root: CommandComponent) {
        root.createTabooLegacyStyleCommandHelper("registry", false)
        root.literal("list", permission = "customvariable.command.registry.list") {
            execute<ProxyCommandSender> { sender, _, _ ->
                sendList(sender, null)
            }
            dynamic("scope", optional = true) {
                suggestion<ProxyCommandSender> { _, _ -> CommandSuggestionProvider.scopes() }
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    val scope = parseScope(sender, ctx.get("scope")) ?: return@execute
                    sendList(sender, scope)
                }
            }
        }
        root.literal("add", permission = "customvariable.command.registry.add") {
            dynamic("scope") {
                suggestion<ProxyCommandSender> { _, _ -> CommandSuggestionProvider.scopes() }
                dynamic("key") {
                    dynamic("type") {
                        suggestion<ProxyCommandSender> { _, _ -> CommandSuggestionProvider.types() }
                        execute<ProxyCommandSender> { sender, ctx, _ ->
                            handleAdd(sender, ctx.get("scope"), ctx.get("key"), ctx.get("type"), null)
                        }
                        dynamic("default", optional = true) {
                            execute<ProxyCommandSender> { sender, ctx, _ ->
                                handleAdd(
                                    sender,
                                    ctx.get("scope"),
                                    ctx.get("key"),
                                    ctx.get("type"),
                                    ctx.get("default")
                                )
                            }
                        }
                    }
                }
            }
        }
        root.literal("del", permission = "customvariable.command.registry.del") {
            dynamic("scope") {
                suggestion<ProxyCommandSender> { _, _ -> CommandSuggestionProvider.scopes() }
                dynamic("key") {
                    execute<ProxyCommandSender> { sender, ctx, _ ->
                        handleDelete(sender, ctx.get("scope"), ctx.get("key"))
                    }
                }
            }
        }
        root.literal("settype", permission = "customvariable.command.registry.settype") {
            dynamic("scope") {
                suggestion<ProxyCommandSender> { _, _ -> CommandSuggestionProvider.scopes() }
                dynamic("key") {
                    dynamic("type") {
                        suggestion<ProxyCommandSender> { _, _ -> CommandSuggestionProvider.types() }
                        execute<ProxyCommandSender> { sender, ctx, _ ->
                            handleSetType(sender, ctx.get("scope"), ctx.get("key"), ctx.get("type"))
                        }
                    }
                }
            }
        }
    }

    private fun handleAdd(
        sender: ProxyCommandSender,
        scopeRaw: String,
        key: String,
        typeRaw: String,
        defaultRaw: String?
    ) {
        val scope = parseScope(sender, scopeRaw) ?: return
        val type = parseType(sender, typeRaw) ?: return

        CommandDatabaseDispatcher.dispatch(sender, "registry-add") {
            val result = RegistryService.register(scope, key, type, defaultRaw, null)
            CommandResultMessenger.send(sender, result) { entry ->
                CustomVariableLang.send(
                    sender,
                    LangKeys.COMMAND_REGISTRY_ADD_OK,
                    entry.scope.name.lowercase(),
                    entry.key,
                    entry.type.name.lowercase()
                )
            }
        }
    }

    private fun handleDelete(sender: ProxyCommandSender, scopeRaw: String, key: String) {
        val scope = parseScope(sender, scopeRaw) ?: return

        CommandDatabaseDispatcher.dispatch(sender, "registry-del") {
            val result = RegistryService.unregister(scope, key)
            CommandResultMessenger.send(sender, result) {
                CustomVariableLang.send(sender, LangKeys.COMMAND_REGISTRY_DEL_OK, scope.name.lowercase(), key)
            }
        }
    }

    private fun handleSetType(sender: ProxyCommandSender, scopeRaw: String, key: String, typeRaw: String) {
        val scope = parseScope(sender, scopeRaw) ?: return
        val type = parseType(sender, typeRaw) ?: return

        CommandDatabaseDispatcher.dispatch(sender, "registry-settype") {
            val result = RegistryService.changeType(scope, key, type)
            CommandResultMessenger.send(sender, result) { entry ->
                CustomVariableLang.send(
                    sender,
                    LangKeys.COMMAND_REGISTRY_SETTYPE_OK,
                    entry.scope.name.lowercase(),
                    entry.key,
                    entry.type.name.lowercase(),
                    entry.defaultRaw ?: ""
                )
            }
        }
    }

    private fun sendList(sender: ProxyCommandSender, scope: VariableScope?) {
        val list = CacheManager.listRegistries(scope)
        CustomVariableLang.send(sender, LangKeys.COMMAND_REGISTRY_LIST_HEADER, list.size)
        if (list.isEmpty()) {
            CustomVariableLang.send(sender, LangKeys.COMMAND_LIST_EMPTY)
            return
        }

        list.forEach { entry ->
            CustomVariableLang.send(
                sender,
                LangKeys.COMMAND_REGISTRY_LIST_ITEM,
                entry.scope.name.lowercase(),
                entry.key,
                entry.type.name.lowercase(),
                entry.enabled,
                entry.defaultRaw ?: ""
            )
        }
    }

    private fun parseScope(sender: ProxyCommandSender, raw: String): VariableScope? {
        val scope = CommandScopeParser.parse(raw)
        if (scope == null) {
            CustomVariableLang.send(sender, LangKeys.ERROR_INVALID_SCOPE, raw)
        }
        return scope
    }

    private fun parseType(sender: ProxyCommandSender, raw: String) = CommandTypeParser.parse(raw).also {
        if (it == null) {
            CustomVariableLang.send(sender, LangKeys.ERROR_INVALID_TYPE, raw)
        }
    }
}


