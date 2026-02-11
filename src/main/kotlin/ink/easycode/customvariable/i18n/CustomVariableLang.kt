package ink.easycode.customvariable.i18n

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.module.lang.asLangText
import taboolib.module.lang.sendLang

object CustomVariableLang {

    fun send(sender: ProxyCommandSender, node: String, vararg args: Any) {
        sender.sendLang(node, *args)
    }

    fun text(sender: ProxyCommandSender, node: String, vararg args: Any): String {
        return sender.asLangText(node, *args)
    }

    fun sendToConsole(node: String, vararg args: Any) {
        console().sendLang(node, *args)
    }
}
