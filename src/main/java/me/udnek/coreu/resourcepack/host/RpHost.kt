package me.udnek.coreu.resourcepack.host

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import me.udnek.coreu.CoreU
import me.udnek.coreu.resourcepack.RpMerger
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.RpInfo
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.resourcepack.misc.at
import me.udnek.coreu.serializabledata.SerializableDataManager
import org.apache.commons.io.FileUtils
import java.net.InetSocketAddress
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path


class RpHost : HttpHandler {

    private var server: HttpServer? = null

    companion object {
        private const val RP_ROOT_AND_NAME = "generated_resourcepack"

        @JvmStatic
        fun getFolderPath(): Path {
            val path: Path = CoreU.getInstance().getDataPath().toAbsolutePath().resolve(RP_ROOT_AND_NAME)
            return path
        }

        @JvmStatic
        fun getZipFilePath(): Path = CoreU.getInstance().getDataPath().resolve("$RP_ROOT_AND_NAME.zip")
    }

    fun compileResourcepack(): Error? {

        val folderPath = getFolderPath()
        Files.createDirectories(folderPath)
        FileUtils.cleanDirectory(folderPath.toFile())

        val merger = RpMerger()
        merger.collectFiles()
        merger.extractTo(folderPath)

        val (checksum, error) = RpHostUtils.calculateFolderSHA(folderPath)
        if (error != null) return error

        val zipFilePath = getZipFilePath()
        val info = SerializableDataManager.read(RpInfo(), CoreU.getInstance())
        if (checksum != info.checksumFolder || !zipFilePath.toFile().exists()) {
            RpHostUtils.zipFolder(folderPath, zipFilePath)
            val (zipSha, error) = RpHostUtils.calculateZipFolderSHA(zipFilePath.toFile())
            if (error != null) return error
            info.checksumZip = zipSha!!
        }
        info.checksumFolder = checksum

        SerializableDataManager.write(info, CoreU.getInstance())
        RpHostUtils.updateServerProperties()
        return null
    }

    fun start(): Error? {
        if (!Files.exists(getZipFilePath())){
            return Error("resourcepack was not generated")
        }
        RpHostUtils.updateServerProperties()

        val rpInfo = SerializableDataManager.read(RpInfo(), CoreU.getInstance())
        val (server, error) = RpUtils.wrapThrowable { HttpServer.create(InetSocketAddress("0.0.0.0", rpInfo.port), 0) }
        if (error != null) return error
        server!!
        server.createContext("/", this)
        this.server = server
        server.start()
        return null
    }

    fun stop(){
        server?.stop(0)
    }

    override fun handle(exchange: HttpExchange) {
        val (fileBytes, error) = RpUtils.wrapThrowable { Files.readAllBytes(getZipFilePath()) }
        if (error != null){
            ("http handle error" at error).logError()
            return
        }
        fileBytes!!

        exchange.getResponseHeaders().set("Content-Type", "application/zip")
        exchange.getResponseHeaders().set(
            "Content-Disposition",
            "attachment; filename*=UTF-8''" + URLEncoder.encode(
                getZipFilePath().getFileName().toString(),
                StandardCharsets.UTF_8
            )
        )
        exchange.sendResponseHeaders(200, fileBytes.size.toLong())
        exchange.getResponseBody().use { s ->
            s.write(fileBytes)
        }
    }
}
