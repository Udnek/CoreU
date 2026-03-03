package me.udnek.coreu.resourcepack

import com.google.gson.JsonObject
import me.udnek.coreu.custom.component.ComponentHolder
import me.udnek.coreu.custom.component.CustomComponentType
import me.udnek.coreu.custom.event.ResourcepackInitializationEvent
import me.udnek.coreu.custom.registry.CustomRegistries
import me.udnek.coreu.resourcepack.file.RpFile
import me.udnek.coreu.resourcepack.file.RpJsonFile
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.resourcepack.misc.ValueOrError
import me.udnek.coreu.util.LogUtils
import net.kyori.adventure.translation.Translatable
import org.bukkit.Bukkit
import java.nio.file.Path

class RpMerger {

    private val allFiles: MutableMap<RpPath, MutableList<RpFile>> = HashMap()
    private val mergedFiles: MutableList<RpFile> = ArrayList()


    fun collectFiles(): Error? {
        allFiles.clear()
        for (plugin in Bukkit.getPluginManager().plugins) {
            if (plugin !is ResourcePackablePlugin) continue
            val (pluginFiles, error) = VirtualResourcePack(plugin).getFiles()
            if (error != null) return error
            pluginFiles!!
            for (file in pluginFiles) {
                addFile(file)
            }
        }

//        for (item in CustomRegistries.ITEM.getAll()) {
//            addFiles(
//                item.getComponents()
//                    .getOrDefault(CustomComponentType.AUTO_GENERATING_FILES_ITEM)
//                    .getFiles(item)
//            )
//        }
//        for (sound in CustomRegistries.SOUND.getAll()) {
//            addFiles(
//                sound.getComponents()
//                    .getOrDefault(CustomComponentType.AUTO_GENERATING_FILES_SOUND)
//                    .getFiles(sound)
//            )
//        }
//        for (registry in CustomRegistries.REGISTRY.getAll()) {
//            for (registrable in registry.getAll()) {
//                if (registrable !is ComponentHolder<*>) continue
//                if (registrable !is Translatable) continue
//                for (file in registrable.getComponents()
//                    .getOrDefault(CustomComponentType.TRANSLATABLE_THING)
//                    .getFiles(registrable, registrable)) {
//                    addFile(file)
//                }
//            }
//        }
//        val event = ResourcepackInitializationEvent()
//        event.callEvent()
//        addFiles(event.files)

        LogUtils.coreuLog("collected files: ${allFiles.size}")
        mergeFiles()
        LogUtils.coreuLog("files after merging: ${mergedFiles.size}")
        return null
    }

    private fun mergeFiles(): Error? {
        for ((path, files) in allFiles) {
            val (chosen, error) = chooseFile(path, files)
            if (error != null) return error
            mergedFiles.add(chosen!!)
        }
        return null
    }

    private fun addFiles(files: List<RpFile>){
        for (file in files) addFile(file)
    }

    private fun addFile(file: RpFile){
        allFiles.compute(file.path(), { _, v ->
            val list = v ?: ArrayList(1)
            list.add(file)
            return@compute list
        })
    }

    fun extractTo(extractPath: Path): Error? {
        for (file in mergedFiles) {
            val extractError = file.extractTo(extractPath)
            if (extractError != null) return extractError
        }
        return null
    }

    private fun chooseFile(path: RpPath, files: MutableList<RpFile>): ValueOrError<RpFile> {
        if (files.size == 1) return ValueOrError.success(files[0])

        // more than one

        // lang
        if (path.matches("assets/*/lang")){
            LogUtils.coreuLog("Merge: ${path}")
            val jsons = ArrayList<JsonObject>()
            for (file in files) {
                val (jsonFile, error) = file.asJson()
                if (error != null) return ValueOrError.failure(error)
                jsonFile!!
                LogUtils.coreuLog("\t${file}")
                jsons.add(jsonFile.json)
            }
            val mergedJson = RpUtils.mergeJsons(jsons)
            return ValueOrError.success(RpJsonFile(files[0].plugin(), path, mergedJson))
        }

        // by priority
        files.sortWith { a, b ->
            Integer.compare(a.plugin()?.priority?.value ?: 99, b.plugin()?.priority?.value ?: 99)
        }
        LogUtils.coreuLog("Sames: ${path}")
        for ((i, file) in files.withIndex()) {
            var str = "\t${file}: (priority: ${file.plugin()?.priority ?: 99})"
            if (i == 0) str = "+ $str"
            LogUtils.coreuLog(str)
        }
        return ValueOrError.success(files[0])
    }
}
