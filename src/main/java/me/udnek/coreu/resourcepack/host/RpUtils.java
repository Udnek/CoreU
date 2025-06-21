package me.udnek.coreu.resourcepack.host;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.resourcepack.RPInfo;
import me.udnek.coreu.serializabledata.SerializableDataManager;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class RpUtils {

    public static void zipFolder(@NotNull Path sourcePath, @NotNull Path zipFilePath) {
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
            Files.walk(sourcePath)
                    .forEach(path -> {
                        try {
                            String entryName = sourcePath.relativize(path).toString()
                                    .replace("\\", "/");

                            if (Files.isDirectory(path)) {
                                if (!entryName.endsWith("/")) {
                                    entryName += "/";
                                }
                                ZipEntry entry = new ZipEntry(entryName);
                                zipOut.putNextEntry(entry);
                                zipOut.closeEntry();
                            } else {
                                ZipEntry entry = new ZipEntry(entryName);

                                if (entryName.equals("pack.mcmeta")) {
                                    entry.setMethod(ZipEntry.STORED); // Без сжатия
                                    entry.setCompressedSize(Files.size(path));
                                    entry.setSize(Files.size(path));
                                    entry.setCrc(calculateCrc32(path));
                                }

                                zipOut.putNextEntry(entry);
                                Files.copy(path, zipOut);
                                zipOut.closeEntry();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Ошибка при упаковке: " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания ZIP-файла", e);
        }
    }

    private static long calculateCrc32(Path file) throws IOException {
        byte[] data = Files.readAllBytes(file);
        CRC32 crc = new CRC32();
        crc.update(data);
        return crc.getValue();
    }

    public static String calculateFolderSHA(Path folderPath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            List<Path> filePaths = new ArrayList<>();
            try (Stream<Path> walk = Files.walk(Paths.get(folderPath.toUri()))) {
                walk.filter(Files::isRegularFile).sorted().forEach(filePaths::add);
            }

            for (Path filePath : filePaths) updateDigest(digest, filePath, folderPath);
            return bytesToHex(digest.digest());

        } catch (NoSuchAlgorithmException | IOException e) {throw new RuntimeException(e);}
    }

    private static void updateDigest(MessageDigest digest, Path filePath, Path basePath) {
        String relativePath = Paths.get(basePath.toUri()).relativize(filePath).toString();
        digest.update(relativePath.getBytes());

        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calculateZipFolderSHA(File file) {
        try (FileInputStream fis = new FileInputStream(file)){
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) hexString.append(String.format("%02x", b));
        return hexString.toString();
    }

    public static void updateServerProperties(){
        try {
            RPInfo rpInfo = SerializableDataManager.read(new RPInfo(), CoreU.getInstance());

            Properties properties = new Properties();
            FileInputStream inStream = new FileInputStream("server.properties");
            properties.load(inStream);
            inStream.close();

            properties.setProperty("resource-pack", "http://" + rpInfo.ip + ":" + rpInfo.port + "/1");
            properties.setProperty("resource-pack-sha1", rpInfo.checksum_zip);

            FileOutputStream fos = new FileOutputStream("server.properties");
            properties.store(fos, "pohui");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
