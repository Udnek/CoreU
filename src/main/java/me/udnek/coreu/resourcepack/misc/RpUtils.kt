package me.udnek.coreu.resourcepack.misc

import com.google.gson.JsonObject
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.isWritable

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

        return wrapThrowable {
            FileSystems.newFileSystem(uri, emptyMap<String, Any>()).use { fs ->
                val jarPath = fs.getPath(dir)
                Files.walk(jarPath).use { stream ->
                    stream
                        .filter { p -> p.isRegularFile() }
                        .forEach { p -> consumer.accept(p) }
                }
            }
        }.error
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















