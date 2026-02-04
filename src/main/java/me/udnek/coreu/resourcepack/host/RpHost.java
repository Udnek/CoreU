package me.udnek.coreu.resourcepack.host;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.resourcepack.RPInfo;
import me.udnek.coreu.serializabledata.SerializableDataManager;
import me.udnek.coreu.util.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@org.jspecify.annotations.NullMarked public class RpHost implements HttpHandler{

    private static final String NAME = "generated_resourcepack";

    public static Path getFolderPath(){
        Path path = CoreU.getInstance().getDataPath().toAbsolutePath().resolve(NAME);
        path.toFile().mkdir();
        return path;
    }

    public static Path getZipFilePath(){
        return CoreU.getInstance().getDataPath().resolve(NAME + ".zip");
    }

    public HttpServer start(){
        if (!Files.exists(getZipFilePath())) LogUtils.pluginWarning("Resourcepack was not generated! Use /resourcepack");
        RpUtils.updateServerProperties();

        try {
            RPInfo rpInfo = SerializableDataManager.read(new RPInfo(), CoreU.getInstance());
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0",rpInfo.port), 0);
            server.createContext("/", this);
            server.start();
            return server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] fileBytes = Files.readAllBytes(getZipFilePath());
        exchange.getResponseHeaders().set("Content-Type", "application/zip");
        exchange.getResponseHeaders().set(
                "Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(getZipFilePath().getFileName().toString(), StandardCharsets.UTF_8));
        exchange.sendResponseHeaders(200, fileBytes.length);
        OutputStream stream = exchange.getResponseBody();
        stream.write(fileBytes);
        stream.close();
    }
}
