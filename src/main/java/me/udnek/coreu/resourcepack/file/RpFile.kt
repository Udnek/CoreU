package me.udnek.coreu.resourcepack.file

import me.udnek.coreu.resourcepack.ResourcePackablePlugin
import me.udnek.coreu.resourcepack.RpPath
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.ValueOrError
import java.nio.file.Path

interface RpFile {

    fun extractTo(extractPath: Path): Error?

    fun path(): RpPath

    fun plugin(): ResourcePackablePlugin?

    fun isSamePath(other: RpFile): Boolean {
        return path().isSame(other.path())
    }

    fun asJson(): ValueOrError<RpJsonFile>
}