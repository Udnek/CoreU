package me.udnek.coreu.resourcepack

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.udnek.coreu.CoreU
import me.udnek.coreu.resourcepack.misc.RpInfo
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.serializabledata.SerializableDataManager
import me.udnek.coreu.util.LogUtils
import org.bukkit.command.ConsoleCommandSender

class ResourcePackCommand : BasicCommand {

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<String>) {
        if (commandSourceStack.sender !is ConsoleCommandSender) {
            commandSourceStack.sender.sendMessage("Command can be executed in console only!")
            return
        }
        if (args.size > 1) return

        val extractDirectory: String
        val info = SerializableDataManager.read(RpInfo(), CoreU.getInstance())

        if (args.size == 1) {
            extractDirectory = args[0]
        } else {
            if (info.extractDirectory == null) {
                LogUtils.coreuLog("Saved directory is null, specify it using argument!")
                return
            }
            LogUtils.coreuLog("Loaded saved directory: " + info.extractDirectory)
            extractDirectory = info.extractDirectory!!
        }
        info.extractDirectory = extractDirectory
        SerializableDataManager.write(info, CoreU.getInstance())

        RpUtils.compileResourcepack(compileServerRp = false, compileLocalAdminRp = true)
    }

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<String>): MutableCollection<String> {
        return mutableListOf()
    }

    override fun permission(): String {
        return "coreu.admin"
    }
}
