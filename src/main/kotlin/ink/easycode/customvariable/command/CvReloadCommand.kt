package ink.easycode.customvariable.command

import ink.easycode.customvariable.bootstrap.PluginBootstrap
import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponent

object CvReloadCommand {

    fun bind(root: CommandComponent) {
        root.execute<ProxyCommandSender> { sender, _, _ ->
            CommandDatabaseDispatcher.dispatch(sender, "reload") {
                val reloaded = PluginBootstrap.reload()
                if (reloaded) {
                    CustomVariableLang.send(sender, LangKeys.COMMAND_RELOAD_OK)
                } else {
                    CustomVariableLang.send(sender, LangKeys.COMMAND_RELOAD_FAILED)
                }
            }
        }
    }
}
