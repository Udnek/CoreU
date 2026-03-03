package me.udnek.coreu.resourcepack.host

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import me.udnek.coreu.CoreU
import me.udnek.coreu.resourcepack.misc.Error
import me.udnek.coreu.resourcepack.misc.RpInfo
import me.udnek.coreu.resourcepack.misc.RpUtils
import me.udnek.coreu.resourcepack.misc.at
import me.udnek.coreu.serializabledata.SerializableDataManager
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
        val folderPath: Path
            get() {
                val path: Path = CoreU.getInstance().getDataPath().toAbsolutePath().resolve(RP_ROOT_AND_NAME)
                return path
            }

        @JvmStatic
        val zipFilePath: Path
            get() = CoreU.getInstance().getDataPath().resolve("$RP_ROOT_AND_NAME.zip")
    }


    fun start(): Error? {
        if (!Files.exists(zipFilePath)){
            return Error("resourcepack was not generated! Use /resourcepack")
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
        val (fileBytes, error) = RpUtils.wrapThrowable { Files.readAllBytes(zipFilePath) }
        if (error != null){
            ("http handle error" at error).logError()
            return
        }
        fileBytes!!

        exchange.getResponseHeaders().set("Content-Type", "application/zip")
        exchange.getResponseHeaders().set(
            "Content-Disposition",
            "attachment; filename*=UTF-8''" + URLEncoder.encode(
                zipFilePath.getFileName().toString(),
                StandardCharsets.UTF_8
            )
        )
        exchange.sendResponseHeaders(200, fileBytes.size.toLong())
        exchange.getResponseBody().use { s ->
            s.write(fileBytes)
        }
    }
}
