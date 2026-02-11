package ink.easycode.customvariable.command

import ink.easycode.customvariable.i18n.CustomVariableLang
import ink.easycode.customvariable.i18n.LangKeys
import ink.easycode.customvariable.sync.SyncManager
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.component.CommandComponent

object CvSyncCommand {

    fun bind(root: CommandComponent) {
        root.execute<ProxyCommandSender> { sender, _, _ ->
            CommandDatabaseDispatcher.dispatch(sender, "sync-now") {
                SyncManager.forceSyncNow()
                val status = SyncManager.status()
                CustomVariableLang.send(
                    sender,
                    LangKeys.COMMAND_SYNC_OK,
                    status.registryQueueSize,
                    status.valueQueueSize,
                    status.lastFlushCostMs,
                    status.lastPullCostMs
                )
            }
        }
    }
}
