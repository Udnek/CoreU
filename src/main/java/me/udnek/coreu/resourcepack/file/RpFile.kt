package me.udnek.coreu.resourcepack.file

import me.udnek.coreu.resourcepack.ResourcePackablePlugin
import me.udnek.coreu.resourcepack.misc.Error
import java.nio.file.Path

interface RpFile {

    fun extractTo(extractPath: Path): Error?

    fun localPath(): String

    fun plugin(): ResourcePackablePlugin;

    fun isSamePath(other: RpFile): Boolean{
        return localPath() == other.localPath()
    }
}