package me.udnek.coreu.resourcepack.file

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.udnek.coreu.resourcepack.ResourcePackablePlugin
import me.udnek.coreu.resourcepack.RpPath
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.ValueOrError
import me.udnek.coreu.resourcepack.misc.at
import me.udnek.coreu.resourcepack.misc.wrapThrowable
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class InJarFile(val plugin: ResourcePackablePlugin, val path: RpPath) : RpFile {

    override fun extractTo(extractPath: Path): Error? {
        return stream { s ->
            val targetPath = extractPath.resolve(path.rpRelative())
            Files.createDirectories(targetPath.parent)
            Files.copy(s, targetPath, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun stream(consumer: (InputStream) -> Unit): Error? {
        val stream = plugin.javaClass.classLoader.getResourceAsStream(path.resourcesRelative())
            ?: return Error("can not get stream: ${path.resourcesRelative()} (${plugin.name})")
        return wrapThrowable {
            stream.use { inputStream -> consumer(inputStream) }
        }.error
    }

    override fun asJson(): ValueOrError<RpJsonFile> {
        var jsonElement: JsonElement? = null
        val error = stream { s ->
            InputStreamReader(s).use { reader ->
                jsonElement = JsonParser.parseReader(com.google.gson.stream.JsonReader(reader))
            }
        }
        if (error != null) return ValueOrError.failure("can not parse json" at error)
        val json = (jsonElement as? JsonObject) ?: return ValueOrError.failure("json is null: $path")
        return ValueOrError.success(RpJsonFile(plugin(), path(), json))
    }

    override fun toString(): String {
        return "InJarFile(plugin=${plugin.name}, path='$path')"
    }

    override fun path(): RpPath = path
    override fun plugin(): ResourcePackablePlugin = plugin
}