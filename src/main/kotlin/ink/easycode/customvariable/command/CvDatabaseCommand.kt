package ink.easycode.customvariable.command

import ink.easycode.customvariable.config.ConfigManager
import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.sync.SyncManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponent
import top.maplex.arim.tools.commandhelper.createTabooLegacyStyleCommandHelper

object CvDatabaseCommand {

    fun bind(root: CommandComponent) {
        root.createTabooLegacyStyleCommandHelper("db", false)
        root.literal("status", permission = "customvariable.command.db.status") {
            execute<ProxyCommandSender> { sender, _, _ ->
                sendStatus(sender)
            }
        }
    }

    private fun sendStatus(sender: ProxyCommandSender) {
        val config = ConfigManager.current()
        val status = SyncManager.status()
        CustomVariableLang.send(
            sender,
            LangKeys.COMMAND_DB_STATUS,
            config.database.type.name.lowercase(),
            config.database.pool.maximumPoolSize,
            config.database.pool.minimumIdle,
            config.database.pool.connectionTimeoutMs
        )
        CustomVariableLang.send(
            sender,
            LangKeys.COMMAND_SYNC_STATUS,
            status.registryQueueSize,
            status.valueQueueSize,
            status.lastFlushAt,
            status.lastFlushCostMs,
            status.lastPullAt,
            status.lastPullCostMs,
            status.consecutiveFailures
        )
    }
}


