package me.udnek.coreu.resourcepack.misc

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.isWritable


fun <T> wrapThrowable(throwing: () -> T): ValueOrError<T> {
    try {
        val res = throwing()
        return ValueOrError.success(res)
    } catch(e: Exception){
        return ValueOrError.failure(e)
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
    if (uri == null) return Error("uri from path is null: $dir")

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
//    val (sourcePath, error) = wrapThrowable { Paths.get(uri) }
//    if (error != null) return Error("can not Paths.get($uri)",error)
//    sourcePath!!
//    for (path in sourcePath.walk()) {
//        println(path)
//    }
//    return null

//    var dirURL = clazz.getClassLoader().getResource(path)
//    if (dirURL != null && dirURL.getProtocol() == "file") {
//        /* A file path: easy enough */
//        return wrapThrowable {
//            for (dir in File(dirURL!!.toURI()).listFiles()) {
//                if (dir.isDirectory()) dirs.accept(dir.path)
//                else files.accept(dir.path)
//            } }.error
//    }
//    if (dirURL == null) {
//        /*
//         * In case of a jar file, we can't actually find a directory.
//         * Have to assume the same jar as clazz.
//         */
//        val me = clazz.getName().replace(".", "/") + ".class"
//        dirURL = clazz.getClassLoader().getResource(me)
//    }
//
//    if (dirURL.getProtocol() != "jar") return Error("cannot list files for URL: $dirURL")
//
//    /* A JAR path */
//    val jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")) //strip out only the JAR file
//    return wrapThrowable {
//        JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8)).use { jar ->
//            val entries = jar.entries() //gives ALL entries in jar
//            val result: MutableSet<String> = HashSet<String>() //avoid duplicates in case it is a subdirectory
//
//            while (entries.hasMoreElements()) {
//                val nextElement = entries.nextElement()
//                val name = nextElement.getName()
//                if (!name.startsWith(path)) continue
//                //filter according to the path
//                var entry = name.substring(path.length)
//                val checkSubDir = entry.indexOf("/")
//                if (checkSubDir >= 0) {
//                    // if it is a subdirectory, we just return the directory name
//                    entry = entry.substring(0, checkSubDir)
//                }
//                // UDNEK
//                if (nextElement.isDirectory()) dirs.accept(entry)
//                else files.accept(entry)
//            }
//            return@wrapThrowable null
//        }
//    }.error