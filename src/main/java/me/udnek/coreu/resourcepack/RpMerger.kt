package me.udnek.coreu.resourcepack

import me.udnek.coreu.resourcepack.file.RpFile
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.util.LogUtils
import org.bukkit.Bukkit
import java.nio.file.Path

class RpMerger {

    private val inJarFiles: MutableMap<String, MutableList<RpFile>> = HashMap()

    fun collectFiles(): Error? {
        inJarFiles.clear()
        for (plugin in Bukkit.getPluginManager().plugins) {
            if (plugin !is ResourcePackablePlugin) continue
            val (pluginFiles, error) = VirtualResourcePack(plugin).getFiles()
            if (error != null) return error
            pluginFiles!!
            for (file in pluginFiles) {
                inJarFiles.compute(file.localPath(), {k, v ->
                    val list = v ?: ArrayList()
                    list.add(file)
                    return@compute list
                })
            }
        }
        LogUtils.pluginLog("InJarFiles: ${inJarFiles}")
        return null
    }

    fun extractTo(extractPath: Path): Error? {
        for ((path, files) in inJarFiles) {
            val file = chooseFile(path, files)
            val error = file.extractTo(extractPath)
            if (error != null) return error
        }
        return null
    }

    private fun chooseFile(path: String, files: MutableList<RpFile>): RpFile {
        if (files.size == 1) return files[0]
        // more than one
        files.sortWith { a, b ->
            Integer.compare(a.plugin().priority.value, b.plugin().priority.value)
        }
        LogUtils.pluginLog("Sames: ${path}")
        for ((i, file) in files.withIndex()) {
            var str = "\t${file}: (priority: ${file.plugin().priority})"
            if (i == 0) str = "+ $str"
            LogUtils.pluginLog(str)
        }
        return files[0]
    }
}
