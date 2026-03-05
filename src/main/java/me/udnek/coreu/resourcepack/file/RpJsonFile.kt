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

class RpJsonFile(val plugin: ResourcePackablePlugin?, val path: RpPath, val json: JsonObject) : RpFile {

    constructor(path: RpPath, json: JsonObject): this(null, path, json)
    constructor(path: String, json: JsonObject): this(RpPath.fromRpRelative(path), json)

    override fun extractTo(extractPath: Path): Error? {
        return RpUtils.wrapThrowable {
            json.toString().byteInputStream().use { inputStream ->
                val targetPath = extractPath.resolve(path.rpRelative())
                Files.createDirectories(targetPath.parent)
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
            }
        }.error
    }

    override fun path(): RpPath = path
    override fun plugin(): ResourcePackablePlugin? = plugin
    override fun asJson(): ValueOrError<RpJsonFile> = ValueOrError.success(this)
}