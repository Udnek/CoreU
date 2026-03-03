package me.udnek.coreu.resourcepack.host

import me.udnek.coreu.CoreU
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.RpInfo
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.resourcepack.misc.ValueOrError
import me.udnek.coreu.resourcepack.misc.at
import me.udnek.coreu.serializabledata.SerializableDataManager
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object RpHostUtils {

    @JvmStatic
    fun zipFolder(sourcePath: Path, zipFilePath: Path): Error? {
         ZipOutputStream(Files.newOutputStream(zipFilePath)).use { zipOut ->
            Files.walk(sourcePath).use { walk ->
                var error: Error? = null
                walk.forEach { path: Path ->
                    error = RpUtils.wrapThrowable ({
                        var entryName = sourcePath.relativize(path).toString()
                            .replace("\\", "/")

                        if (Files.isDirectory(path)) {
                            if (!entryName.endsWith("/")) {
                                entryName += "/"
                            }
                            val entry = ZipEntry(entryName)
                            zipOut.putNextEntry(entry)
                            zipOut.closeEntry()
                        } else {
                            val entry = ZipEntry(entryName)

                            if (entryName == "pack.mcmeta") {
                                entry.setMethod(ZipEntry.STORED) // Без сжатия
                                entry.setCompressedSize(Files.size(path))
                                entry.setSize(Files.size(path))
                                val (crc, error) = calculateCrc32(path)
                                if (error != null) return@wrapThrowable error
                                entry.setCrc(crc!!)
                            }

                            zipOut.putNextEntry(entry)
                            Files.copy(path, zipOut)
                            zipOut.closeEntry()
                        }
                    }, { e -> ValueOrError.failure("Can not zip: " + path +": " + e.stackTraceToString()) }).error
                }
                if (error != null) return@use ValueOrError.failure<Nothing>(error!!)
            }
        }
        return null
    }

    @JvmStatic
    private fun calculateCrc32(file: Path): ValueOrError<Long> {
        val (data, error) = RpUtils.wrapThrowable { Files.readAllBytes(file) }
        if (error != null) return ValueOrError.failure("can not calc crc32" at error)
        val crc = CRC32()
        crc.update(data)
        return ValueOrError.success(crc.getValue())
    }

    @JvmStatic
    fun calculateFolderSHA(folderPath: Path): ValueOrError<String> {
        val (digest, error) = RpUtils.wrapThrowable{MessageDigest.getInstance("SHA-1")}
        if (error != null) return ValueOrError.failure("can not calculate folder SHA-1" at error)
        digest!!

        val filePaths: MutableList<Path> = ArrayList<Path>()
        Files.walk(Paths.get(folderPath.toUri())).use { walk ->
            walk.filter { path: Path -> Files.isRegularFile(path) }
                .sorted()
                .forEach { e: Path -> filePaths.add(e) }
        }
        for (filePath in filePaths) {
            val digError = updateDigest(digest, filePath, folderPath)
            if (digError != null) return ValueOrError.failure(digError)
        }
        return ValueOrError.success(bytesToHex(digest.digest()))
    }

    @JvmStatic
    private fun updateDigest(digest: MessageDigest, filePath: Path, basePath: Path): Error? {
        val relativePath = Paths.get(basePath.toUri()).relativize(filePath).toString()
        digest.update(relativePath.toByteArray())
        return RpUtils.wrapThrowable {
            Files.newInputStream(filePath).use { inputStream ->
                BufferedInputStream(inputStream).use { buffInputStream ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while ((buffInputStream.read(buffer).also { bytesRead = it }) != -1) {
                        digest.update(buffer, 0, bytesRead)
                    }
                }
            }
        }.error
    }

    @JvmStatic
    fun calculateZipFolderSHA(file: File): ValueOrError<String> {
        return RpUtils.wrapThrowable {
            FileInputStream(file).use { fis ->
                val digest = MessageDigest.getInstance("SHA-1")
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while ((fis.read(buffer).also { bytesRead = it }) != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
                return@wrapThrowable bytesToHex(digest.digest())
            }
        }
    }

    @JvmStatic
    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (b in bytes) hexString.append(String.format("%02x", b))
        return hexString.toString()
    }

    @JvmStatic
    fun updateServerProperties() {
        val rpInfo = SerializableDataManager.read(RpInfo(), CoreU.getInstance())

        val properties = Properties()
        FileInputStream("server.properties").use { stream ->
            properties.load(stream)
        }

        properties.setProperty("resource-pack", "http://" + rpInfo.ip + ":" + rpInfo.port + "/1")
        properties.setProperty("resource-pack-sha1", rpInfo.checksumZip)

        FileOutputStream("server.properties").use { stream ->
            properties.store(stream, "")
        }
    }
}
