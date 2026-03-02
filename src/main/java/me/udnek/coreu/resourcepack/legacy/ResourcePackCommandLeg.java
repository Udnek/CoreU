package me.udnek.coreu.resourcepack.legacy;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.resourcepack.host.RpHost;
import me.udnek.coreu.resourcepack.host.RpHostUtils;
import me.udnek.coreu.resourcepack.legacy.merger.RpMergerLeg;
import me.udnek.coreu.resourcepack.misc.RpInfo;
import me.udnek.coreu.serializabledata.SerializableDataManager;
import me.udnek.coreu.util.LogUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

@org.jspecify.annotations.NullMarked
public class ResourcePackCommandLeg implements BasicCommand{

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getSender() instanceof ConsoleCommandSender)){
            commandSourceStack.getSender().sendMessage("Command can be executed in console only!");
            return;
        }

        if (args.length > 1) return;

        RpInfo info = SerializableDataManager.read(new RpInfo(), CoreU.getInstance());

        if (args.length == 1) {
            info.extractDirectory = args[0];
        }
        else {
            if (info.extractDirectory == null){
                LogUtils.coreuLog("Saved directory is null, specify it using argument!");
                return;
            }
            LogUtils.coreuLog("Loaded saved directory: " + info.extractDirectory);
        }

        RpMergerLeg merger = new RpMergerLeg();
        String error = merger.checkExtractDirectoryAndError(info.extractDirectory);
        if (error != null){
            LogUtils.coreuLog(error);
            return;
        }

        assert info.extractDirectory != null;
        merger.startMergeInto(info.extractDirectory);

        try {
            RpMergerLeg mergerHost = new RpMergerLeg();
            Path path = RpHost.getFolderPath();
            Files.createDirectories(path);
            FileUtils.cleanDirectory(path.toFile());
            mergerHost.startMergeInto(path.toString());

            String checksum = RpHostUtils.calculateFolderSHA(path);
            Path zipFilePath = RpHost.getZipFilePath();
            if (!checksum.equals(info.checksum_folder) || !RpHost.getZipFilePath().toFile().exists()){
                RpHostUtils.zipFolder(RpHost.getFolderPath(), zipFilePath);
                info.checksum_zip = RpHostUtils.calculateZipFolderSHA(zipFilePath.toFile());
            }
            info.checksum_folder = checksum;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SerializableDataManager.write(info, CoreU.getInstance());
        RpHostUtils.updateServerProperties();

        LogUtils.coreuWarning("If your sound does not play, remove '<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>' in plugin's pom!");
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return List.of();
    }

    @Override
    public @Nullable String permission() {
        return "coreu.admin";
    }
}
