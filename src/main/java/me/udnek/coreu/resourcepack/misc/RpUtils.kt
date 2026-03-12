package me.udnek.coreu.resourcepack.misc

import com.google.gson.JsonObject
import me.udnek.coreu.CoreU
import me.udnek.coreu.resourcepack.RpMerger
import me.udnek.coreu.resourcepack.host.RpHost
import me.udnek.coreu.resourcepack.host.RpHostUtils
import me.udnek.coreu.serializabledata.SerializableDataManager
import me.udnek.coreu.util.LogUtils
import org.apache.commons.io.FileUtils
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration.between
import java.time.Instant
import java.util.function.Consumer
import kotlin.io.path.*

object RpUtils {
    fun <T> wrapThrowable(throwing: () -> T): ValueOrError<T> =
        wrapThrowable(throwing) { e -> ValueOrError.failure(e) }

    fun <T> wrapThrowable(throwing: () -> T, onError: (Exception) -> ValueOrError<T>): ValueOrError<T> {
        try {
            val res = throwing()
            return ValueOrError.success(res)
        } catch(e: Exception){
            return onError(e)
        }
    }

    @JvmStatic
    fun compileResourcepack(compileServerRp: Boolean, compileLocalAdminRp: Boolean): Error?{
        if (!compileServerRp && !compileLocalAdminRp) return null
        val start = Instant.now()

        val info = SerializableDataManager.read(RpInfo(), CoreU.getInstance())
        val extractPaths = ArrayList<Path>(2)
        if (compileLocalAdminRp){
            val (adminPath, error) = checkCorrectExtractDirectory(info.extractDirectory)
            if (error != null) return Error("can not compile admin rp").at(error)

            extractPaths.add(adminPath!!)
        }

        if (compileServerRp){
            val serverRpPath = RpHost.getFolderPath()
            serverRpPath.createDirectories() // so cleanDirectories won't error
            FileUtils.cleanDirectory(serverRpPath.toFile())

            extractPaths.add(serverRpPath)
        }

        val merger = RpMerger()
        merger.collectFiles()
        for (path in extractPaths) {
            val mergeError = merger.extractTo(path)
            if (mergeError != null) return mergeError
        }

        if (compileServerRp) {
            val serverRpPath = RpHost.getFolderPath()
            val (checksum, checksumError) = RpHostUtils.calculateFolderSHA(serverRpPath)
            if (checksumError != null) return checksumError

            val zipFilePath = RpHost.getZipFilePath()
            if (checksum != info.checksumFolder || !zipFilePath.exists()) {
                val zipError = RpHostUtils.zipFolder(serverRpPath, zipFilePath)
                if (zipError != null) return zipError
                val (zipSha, zipShaError) = RpHostUtils.calculateZipFolderSHA(zipFilePath.toFile())
                if (zipShaError != null) return zipShaError
                info.checksumZip = zipSha!!
            }
            info.checksumFolder = checksum

            RpHostUtils.updateServerProperties(info)
        }

        SerializableDataManager.write(info, CoreU.getInstance())

        LogUtils.coreuLog("ResourcePack extracted! (${between(start, Instant.now()).toMillis()}ms)")
        return null
    }

    fun checkCorrectExtractDirectory(dir: String?): ValueOrError<Path> {
        if (dir == null) return ValueOrError.failure("directory can not be null!")
        val path = Path.of(dir)
        if (!path.isDirectory()) return ValueOrError.failure("specified path is not a directory!")
        if (!path.isAbsolute) return ValueOrError.failure("directory must be absolute!")
        if (!path.exists()) return ValueOrError.failure("directory does not exists!")
        if (!path.isWritable()) return ValueOrError.failure("directory can not be written!")
        return ValueOrError.success(path)
    }

    fun getAllResources(clazz: Class<*>, dir: String, consumer: Consumer<Path>): Error? {
        val uri = clazz.classLoader.getResource(dir)?.toURI()
            ?: return Error("uri from path is null: $dir")

        FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fs ->
            val jarPath = fs.getPath(dir)
            Files.walk(jarPath).use { stream ->
                stream
                    .filter { p -> p.isRegularFile() }
                    .forEach { p -> consumer.accept(p) }
            }
        }
        return null
    }

    fun mergeJsons(jsons: List<JsonObject>): JsonObject {
        val merged = JsonObject()
        for (json in jsons) {
            for (entry in json.asMap().entries) {
                merged.add(entry.key, entry.value)
            }
        }
        return merged
    }
}















