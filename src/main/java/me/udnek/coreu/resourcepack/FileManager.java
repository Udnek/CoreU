package me.udnek.coreu.resourcepack;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileManager{
    public static @NotNull String layerUp(@NotNull String path){
        path = removeSlashes(path);
        int i = path.lastIndexOf("/");
        if (i == -1) return "";
        return path.substring(0, i);
    }

    public static @NotNull String removeSlashes(@NotNull String path){
        if (path.startsWith("/")) path = path.substring(1);
        if (path.endsWith("/")) path = path.substring(0, path.length()-1);
        return path;
    }
    public static @NotNull String joinPaths(@NotNull String first, @NotNull String second){
        first = removeSlashes(first);
        second = removeSlashes(second);
        return first + "/" + second;
    }

    public static @NotNull InputStream getInputStream(@NotNull Class<?> clazz, @NotNull String path){
        Preconditions.checkArgument(isFile(clazz, path), "Can not read file: " + path);
        return clazz.getClassLoader().getResourceAsStream(path);
    }


    public static boolean isFile(@NotNull Class<?> clazz, @NotNull String path){
        URL resource = clazz.getClassLoader().getResource(path);
        if (resource == null) return false;
        return FileType.get(removeSlashes(resource.getPath())) != FileType.UNKNOWN;
    }

    public static boolean isDirectoryEmpty(@NotNull Class<?> clazz, @NotNull String path){
        List<String> resources = getAllResources(clazz, path);
        return resources.isEmpty();
    }

    public static @NotNull List<String> getAllResources(@NotNull Class<?> clazz, @NotNull String path){
        path = removeSlashes(path) + "/";
        String[] allResources = getAllResourcesInternal(clazz, path);
        List<String> result = new ArrayList<>();
        for (String resource : allResources) {
            if (resource == null || resource.isEmpty()) continue;
            result.add(resource);
        }
        return result;
    }

    private static @NotNull String[] getAllResourcesInternal(@NotNull Class<?> clazz, @NotNull String path){
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            try {
                return new File(dirURL.toURI()).list();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory.
             * Have to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/")+".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            /* A JAR path */
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
            try (
                JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))
            ){
                Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                Set<String> result = new HashSet<>(); //avoid duplicates in case it is a subdirectory
                while(entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    if (name.startsWith(path)) { //filter according to the path
                        String entry = name.substring(path.length());
                        int checkSubdir = entry.indexOf("/");
                        if (checkSubdir >= 0) {
                            // if it is a subdirectory, we just return the directory name
                            entry = entry.substring(0, checkSubdir);
                        }
                        result.add(entry);
                    }
                }
                return result.toArray(new String[result.size()]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
    }
}
