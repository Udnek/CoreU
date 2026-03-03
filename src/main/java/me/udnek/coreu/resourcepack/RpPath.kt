package me.udnek.coreu.resourcepack

import java.nio.file.Path
import java.nio.file.Paths

class RpPath private constructor(private val relative: String) {

    companion object {
        const val ROOT_RESOURCES_DIR = "resourcepack"
        private val ROOT_RESOURCES_PATH = Paths.get(ROOT_RESOURCES_DIR)

        fun fromRpRelative(path: String): RpPath {
            return RpPath(path)
        }

        fun fromResources(path: String): RpPath{
            return RpPath(ROOT_RESOURCES_PATH.relativize(Paths.get(path)).toString())
        }
        fun fromResources(path: Path): RpPath{
            return fromResources(path.toString())
        }
    }

    fun matches(regex: String): Boolean {
        return relative.matches((regex.replace("*", ".*") + ".*").toRegex())
    }

    fun resourcesRelative(): String = ROOT_RESOURCES_PATH.resolve(Paths.get(relative)).toString()
        .replace("\\", "/") // getResources uses unix slashes

    fun rpRelative(): String = relative

    override fun toString(): String = rpRelative()

    fun isSame(other: RpPath): Boolean{
        return this.relative == other.relative
    }

    override fun equals(other: Any?): Boolean {
        return ((other as? RpPath)?: return false).relative == this.relative
    }
    override fun hashCode(): Int = relative.hashCode()
}