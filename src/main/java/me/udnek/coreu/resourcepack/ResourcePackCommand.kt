package me.udnek.coreu.resourcepack

import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.udnek.coreu.CoreU
import me.udnek.coreu.resourcepack.misc.RpInfo
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.serializabledata.SerializableDataManager
import me.udnek.coreu.util.LogUtils
import org.bukkit.command.ConsoleCommandSender
import java.time.Duration.between
import java.time.Instant

class ResourcePackCommand : BasicCommand {

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<String>) {
        if (commandSourceStack.sender !is ConsoleCommandSender) {
            commandSourceStack.sender.sendMessage("Command can be executed in console only!")
            return
        }

        if (args.size > 1) return

        val info = SerializableDataManager.read(RpInfo(), CoreU.getInstance())

        if (args.size == 1) {
            info.extractDirectory = args[0]
        } else {
            if (info.extractDirectory == null) {
                LogUtils.coreuLog("Saved directory is null, specify it using argument!")
                return
            }
            LogUtils.coreuLog("Loaded saved directory: " + info.extractDirectory)
        }

        var (path, error) = RpUtils.checkCorrectExtractDirectory(info.extractDirectory)
        if (error != null) {
            error.logError()
            return
        }
        path!!

        // saves new path
        SerializableDataManager.write(info, CoreU.getInstance())

        val start = Instant.now()

        val merger = RpMerger()
        error = merger.collectFiles()
        if (error != null) {
            error.logError()
            return
        }
        error = merger.extractTo(path)
        if (error != null) {
            error.logError()
            return
        }

        LogUtils.coreuLog("ResourcePack extracted! (${between(start, Instant.now()).toMillis()}ms)")

//        try {
//            val mergerHost = RpMergerLeg()
//            val path = RpHost.getFolderPath()
//            Files.createDirectories(path)
//            FileUtils.cleanDirectory(path.toFile())
//            mergerHost.startMergeInto(path.toString())
//
//            val checksum = RpUtils.calculateFolderSHA(path)
//            val zipFilePath = RpHost.getZipFilePath()
//            if (checksum != info.checksum_folder || !RpHost.getZipFilePath().toFile().exists()) {
//                RpUtils.zipFolder(RpHost.getFolderPath(), zipFilePath)
//                info.checksum_zip = RpUtils.calculateZipFolderSHA(zipFilePath.toFile())
//            }
//            info.checksum_folder = checksum
//        } catch (e: IOException) {
//            throw RuntimeException(e)
//        }
//
//        SerializableDataManager.write(info, CoreU.getInstance())
//        RpUtils.updateServerProperties()
//
//        LogUtils.pluginWarning("If your sound does not play, remove '<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>' in plugin's pom!")
    }

    override fun suggest(commandSourceStack: CommandSourceStack, args: Array<String>): MutableCollection<String> {
        return mutableListOf<String>()
    }

    override fun permission(): String? {
        return "coreu.admin"
    }
}
