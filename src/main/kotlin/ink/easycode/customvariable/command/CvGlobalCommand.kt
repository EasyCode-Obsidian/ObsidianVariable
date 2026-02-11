package ink.easycode.customvariable.command

import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.service.ValueService
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponent
import top.maplex.arim.tools.commandhelper.createTabooLegacyStyleCommandHelper

object CvGlobalCommand {

    fun bind(root: CommandComponent) {
        root.createTabooLegacyStyleCommandHelper("global", false)
        root.literal("list", permission = "customvariable.command.global.list") {
            execute<ProxyCommandSender> { sender, _, _ ->
                val result = ValueService.listGlobal()
                CommandResultMessenger.send(sender, result) { values ->
                    CustomVariableLang.send(sender, LangKeys.COMMAND_GLOBAL_LIST_HEADER, values.size)
                    if (values.isEmpty()) {
                        CustomVariableLang.send(sender, LangKeys.COMMAND_LIST_EMPTY)
                    } else {
                        values.forEach { value ->
                            CustomVariableLang.send(sender, LangKeys.COMMAND_GLOBAL_LIST_ITEM, value.key, value.rawValue)
                        }
                    }
                }
            }
        }
        root.literal("get", permission = "customvariable.command.global.get") {
            dynamic("key") {
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    val key = ctx.get("key")
                    val result = ValueService.getGlobal(key)
                    CommandResultMessenger.send(sender, result) { value ->
                        CustomVariableLang.send(sender, LangKeys.COMMAND_GLOBAL_GET_OK, key, value)
                    }
                }
            }
        }
        root.literal("set", permission = "customvariable.command.global.set") {
            dynamic("key") {
                dynamic("value") {
                    execute<ProxyCommandSender> { sender, ctx, _ ->
                        val key = ctx.get("key")
                        val value = ctx.get("value")
                        val result = ValueService.setGlobal(key, value)
                        CommandResultMessenger.send(sender, result) {
                            CustomVariableLang.send(sender, LangKeys.COMMAND_GLOBAL_SET_OK, key, value)
                        }
                    }
                }
            }
        }
        root.literal("del", permission = "customvariable.command.global.del") {
            dynamic("key") {
                execute<ProxyCommandSender> { sender, ctx, _ ->
                    val key = ctx.get("key")
                    val result = ValueService.deleteGlobal(key)
                    CommandResultMessenger.send(sender, result) {
                        CustomVariableLang.send(sender, LangKeys.COMMAND_GLOBAL_DEL_OK, key)
                    }
                }
            }
        }
    }
}


