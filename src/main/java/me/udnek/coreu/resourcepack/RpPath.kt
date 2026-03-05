package me.udnek.coreu.resourcepack

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths

class RpPath private constructor(private val relative: Path) {

    companion object {
        const val ROOT_RESOURCES_DIR = "resourcepack"
        private val ROOT_RESOURCES_PATH = Paths.get(ROOT_RESOURCES_DIR)

        fun fromRpRelative(path: String): RpPath {
            return RpPath(Path.of(path))
        }

        fun fromResources(path: String): RpPath{
            return RpPath(ROOT_RESOURCES_PATH.relativize(Paths.get(path)))
        }
        fun fromResources(path: Path): RpPath{
            return fromResources(path.toString())
        }
    }

    fun matches(regex: String): Boolean {
        return relative.toString().replace("\\", "/")
            .matches((regex.replace("\\", "/").replace("*", ".*") + ".*").toRegex())
    }

    fun resourcesRelative(): String = ROOT_RESOURCES_PATH.resolve(relative).toString()
        .replace("\\", "/") // getResources uses unix slashes

    fun rpRelative(): String = relative.toString()

    override fun toString(): String = rpRelative()

    fun isSame(other: RpPath): Boolean{
        return this.relative == other.relative
    }

    override fun equals(other: Any?): Boolean {
        val otherPath = (other as? RpPath) ?: return false
        return isSame(otherPath)
    }
    override fun hashCode(): Int = relative.hashCode()
}