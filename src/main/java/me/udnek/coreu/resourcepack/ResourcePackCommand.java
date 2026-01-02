package me.udnek.coreu.resourcepack;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.resourcepack.host.RpHost;
import me.udnek.coreu.resourcepack.host.RpUtils;
import me.udnek.coreu.resourcepack.merger.RpMerger;
import me.udnek.coreu.serializabledata.SerializableDataManager;
import me.udnek.coreu.util.LogUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ResourcePackCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (!(commandSourceStack.getSender() instanceof ConsoleCommandSender)){
            commandSourceStack.getSender().sendMessage("Command can be executed in console only!");
            return;
        }

        if (args.length > 1) return;


        RPInfo info = SerializableDataManager.read(new RPInfo(), CoreU.getInstance());

        if (args.length == 1) {
            info.extractDirectory = args[0];
        }
        else {
            if (info.extractDirectory == null){
                LogUtils.pluginLog("Saved directory is null, specify it using argument!");
                return;
            }
            LogUtils.pluginLog("Loaded saved directory: " + info.extractDirectory);
        }

        RpMerger merger = new RpMerger();
        String error = merger.checkExtractDirectoryAndError(info.extractDirectory);
        if (error != null){
            LogUtils.pluginLog(error);
            return;
        }

        assert info.extractDirectory != null;
        merger.startMergeInto(info.extractDirectory);

        try {
            RpMerger mergerHost = new RpMerger();
            Path path = RpHost.getFolderPath();
            Files.createDirectories(path);
            FileUtils.cleanDirectory(path.toFile());
            mergerHost.startMergeInto(path.toString());

            String checksum = RpUtils.calculateFolderSHA(path);
            Path zipFilePath = RpHost.getZipFilePath();
            if (!checksum.equals(info.checksum_folder) || !RpHost.getZipFilePath().toFile().exists()){
                RpUtils.zipFolder(RpHost.getFolderPath(), zipFilePath);
                info.checksum_zip = RpUtils.calculateZipFolderSHA(zipFilePath.toFile());
            }
            info.checksum_folder = checksum;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SerializableDataManager.write(info, CoreU.getInstance());
        RpUtils.updateServerProperties();

        LogUtils.pluginWarning("If your sound does not play, remove '<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>' in plugin's pom!");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        return List.of();
    }


    @Override
    public @org.jspecify.annotations.Nullable String permission() {
        return "coreu.admin";
    }
}
