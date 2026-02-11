package ink.easycode.customvariable.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import top.maplex.arim.tools.commandhelper.createTabooLegacyStyleCommandHelper

@CommandHeader(
    name = "cv",
    aliases = ["customvariable"],
    description = "CustomVariable command",
    usage = "/cv",
    permission = "customvariable.command.use"
)
object CvCommand {

    @CommandBody(permission = "customvariable.command.help")
    val main = mainCommand {
        createTabooLegacyStyleCommandHelper("cv", true)
    }

    @CommandBody(permission = "customvariable.command.help")
    val help = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            sender.performCommand("cv")
        }
    }

    @CommandBody(permission = "customvariable.command.registry")
    val registry = subCommand {
        CvRegistryCommand.bind(this)
    }

    @CommandBody(permission = "customvariable.command.global")
    val global = subCommand {
        CvGlobalCommand.bind(this)
    }

    @CommandBody(permission = "customvariable.command.player")
    val player = subCommand {
        CvPlayerCommand.bind(this)
    }

    @CommandBody(permission = "customvariable.command.sync")
    val sync = subCommand {
        CvSyncCommand.bind(this)
    }

    @CommandBody(permission = "customvariable.command.reload")
    val reload = subCommand {
        CvReloadCommand.bind(this)
    }

    @CommandBody(permission = "customvariable.command.db")
    val db = subCommand {
        CvDatabaseCommand.bind(this)
    }
}


