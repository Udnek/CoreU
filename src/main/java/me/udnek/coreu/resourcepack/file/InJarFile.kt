package me.udnek.coreu.resourcepack.file

import me.udnek.coreu.resourcepack.ResourcePackablePlugin
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.wrapThrowable
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class InJarFile(val plugin: ResourcePackablePlugin, val resourcePath: String) : RpFile {

    companion object {
        val BASE_PATH = "resourcepack"
    }

    override fun extractTo(extractPath: Path): Error? {
        val stream = plugin.javaClass.classLoader.getResourceAsStream(resourcePath)
        if (stream == null) return Error("can not get stream: $resourcePath (${plugin.name})")
        val error = wrapThrowable {
            stream.use { inputStream ->
                val targetPath = extractPath.resolve(resourcePath.substring(BASE_PATH.length + 1))
                Files.createDirectories(targetPath.parent)
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }.error
        if (error != null) {
            return Error("can not extract", error)
        }
        return null
    }

    override fun toString(): String {
        return "InJarFile(plugin=${plugin.name}, path='$resourcePath')"
    }

    override fun localPath(): String = resourcePath
    override fun plugin(): ResourcePackablePlugin = plugin
}