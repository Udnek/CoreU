package me.udnek.coreu.resourcepack

import me.udnek.coreu.resourcepack.file.InJarFile
import me.udnek.coreu.resourcepack.misc.ValueOrError
import me.udnek.coreu.resourcepack.misc.getAllResources

class VirtualResourcePack(val plugin: ResourcePackablePlugin) {

    private var files: List<InJarFile>? = null

    fun getFiles(): ValueOrError<List<InJarFile>> {
        if (files == null){
            val inJarFiles = ArrayList<InJarFile>()
            val error = getAllResources(plugin.javaClass, RpPath.ROOT_RESOURCES_DIR,
                {path -> inJarFiles.add(InJarFile(plugin, RpPath.fromResources(path)))})
            if (error != null) return ValueOrError.failure(error)
            files = inJarFiles
        }
        return ValueOrError.success(files!!)
    }
}














