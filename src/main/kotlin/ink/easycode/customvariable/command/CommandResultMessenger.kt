package ink.easycode.customvariable.command

import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.model.ServiceErrorCode
import ink.easycode.customvariable.model.ServiceResult
import taboolib.common.platform.ProxyCommandSender

object CommandResultMessenger {

    fun <T> send(
        sender: ProxyCommandSender,
        result: ServiceResult<T>,
        onOk: (T) -> Unit
    ) {
        when (result) {
            is ServiceResult.Ok -> onOk(result.value)
            is ServiceResult.Error -> sendError(sender, result)
        }
    }

    fun sendError(sender: ProxyCommandSender, error: ServiceResult.Error) {
        when (error.code) {
            ServiceErrorCode.INVALID_KEY -> send(sender, LangKeys.ERROR_INVALID_KEY, error.args)
            ServiceErrorCode.INVALID_TYPE_VALUE -> send(sender, LangKeys.ERROR_INVALID_TYPE_VALUE, error.args)
            ServiceErrorCode.VARIABLE_NOT_REGISTERED -> send(sender, LangKeys.ERROR_VARIABLE_NOT_REGISTERED, error.args)
            ServiceErrorCode.VARIABLE_ALREADY_REGISTERED -> send(sender, LangKeys.ERROR_VARIABLE_ALREADY_REGISTERED, error.args)
            ServiceErrorCode.REGISTRY_DISABLED -> send(sender, LangKeys.ERROR_REGISTRY_DISABLED, error.args)
            ServiceErrorCode.PLAYER_NOT_FOUND -> send(sender, LangKeys.ERROR_PLAYER_NOT_FOUND, error.args)
            ServiceErrorCode.DATABASE_ERROR -> send(sender, LangKeys.ERROR_DATABASE, error.args)
            ServiceErrorCode.INTERNAL_ERROR -> send(sender, LangKeys.ERROR_INTERNAL, error.args)
        }
    }

    private fun send(sender: ProxyCommandSender, node: String, args: Array<out Any>) {
        CustomVariableLang.send(sender, node, *args)
    }
}
