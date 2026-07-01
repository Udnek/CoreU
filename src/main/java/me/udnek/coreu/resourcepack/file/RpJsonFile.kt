package me.udnek.coreu.resourcepack.file

import com.google.gson.JsonObject
import me.udnek.coreu.resourcepack.ResourcePackablePlugin
import me.udnek.coreu.resourcepack.RpPath
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.resourcepack.misc.ValueOrError
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class RpJsonFile private constructor(val sourceObject: Any, val priority: ResourcePackablePlugin.Priority, val path: RpPath, val json: JsonObject) : RpFile {

    constructor(sourceObject: Any, path: RpPath, json: JsonObject):
            this(sourceObject,
        (sourceObject as? ResourcePackablePlugin)?.priority ?: ResourcePackablePlugin.Priority.RUNTIME,
        path, json){

    }
    constructor(sourceObject: Any, path: String, json: JsonObject): this(sourceObject, RpPath.fromRpRelative(path), json)

    override fun extractTo(extractPath: Path): Error? {
        return RpUtils.wrapThrowable {
            json.toString().byteInputStream().use { inputStream ->
                val targetPath = extractPath.resolve(path.rpRelative())
                Files.createDirectories(targetPath.parent)
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }.error
    }

    override fun toString(): String {
        return "RpJsonFile(source=${sourceObject}, path='$path')"
    }

    override fun path(): RpPath = path
    override fun priority(): ResourcePackablePlugin.Priority = priority
    override fun asJson(): ValueOrError<RpJsonFile> = ValueOrError.success(this)
}