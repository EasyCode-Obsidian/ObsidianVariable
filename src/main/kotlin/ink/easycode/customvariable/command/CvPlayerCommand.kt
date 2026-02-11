package ink.easycode.customvariable.command

import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.model.ServiceResult
import ink.easycode.customvariable.service.ValueService
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponent
import top.maplex.arim.tools.commandhelper.createTabooLegacyStyleCommandHelper

object CvPlayerCommand {

    fun bind(root: CommandComponent) {
        root.createTabooLegacyStyleCommandHelper("player", false)
        root.literal("list", permission = "customvariable.command.player.list") {
            dynamic("owner") {
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    val ownerId = resolveOwner(sender, ctx.get("owner")) ?: return@execute
                    val result = ValueService.listPlayer(ownerId)
                    CommandResultMessenger.send(sender, result) { values ->
                        CustomVariableLang.send(sender, LangKeys.COMMAND_PLAYER_LIST_HEADER, ownerId, values.size)
                        if (values.isEmpty()) {
                            CustomVariableLang.send(sender, LangKeys.COMMAND_LIST_EMPTY)
                        } else {
                            values.forEach { value ->
                                CustomVariableLang.send(
                                    sender,
                                    LangKeys.COMMAND_PLAYER_LIST_ITEM,
                                    ownerId,
                                    value.key,
                                    value.rawValue
                                )
                            }
                        }
                    }
                }
            }
        }
        root.literal("get", permission = "customvariable.command.player.get") {
            dynamic("owner") {
                dynamic("key") {
                    execute<ProxyCommandSender> { sender, ctx, _ ->
                        val ownerId = resolveOwner(sender, ctx.get("owner")) ?: return@execute
                        val key = ctx.get("key")
                        val result = ValueService.getPlayer(ownerId, key)
                        CommandResultMessenger.send(sender, result) { value ->
                            CustomVariableLang.send(sender, LangKeys.COMMAND_PLAYER_GET_OK, ownerId, key, value)
                        }
                    }
                }
            }
        }
        root.literal("set", permission = "customvariable.command.player.set") {
            dynamic("owner") {
                dynamic("key") {
                    dynamic("value") {
                        execute<ProxyCommandSender> { sender, ctx, _ ->
                            val ownerId = resolveOwner(sender, ctx.get("owner")) ?: return@execute
                            val key = ctx.get("key")
                            val value = ctx.get("value")
                            val result = ValueService.setPlayer(ownerId, key, value)
                            CommandResultMessenger.send(sender, result) {
                                CustomVariableLang.send(sender, LangKeys.COMMAND_PLAYER_SET_OK, ownerId, key, value)
                            }
                        }
                    }
                }
            }
        }
        root.literal("del", permission = "customvariable.command.player.del") {
            dynamic("owner") {
                dynamic("key") {
                    execute<ProxyCommandSender> { sender, ctx, _ ->
                        val ownerId = resolveOwner(sender, ctx.get("owner")) ?: return@execute
                        val key = ctx.get("key")
                        val result = ValueService.deletePlayer(ownerId, key)
                        CommandResultMessenger.send(sender, result) {
                            CustomVariableLang.send(sender, LangKeys.COMMAND_PLAYER_DEL_OK, ownerId, key)
                        }
                    }
                }
            }
        }
    }

    private fun resolveOwner(sender: ProxyCommandSender, raw: String): String? {
        val result = CommandOwnerResolver.resolve(raw)
        return when (result) {
            is ServiceResult.Ok -> result.value
            is ServiceResult.Error -> {
                CommandResultMessenger.sendError(sender, result)
                null
            }
        }
    }
}


