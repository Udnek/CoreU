package me.udnek.coreu.custom.component.instance;

import com.google.gson.JsonParser;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.sound.ConstructableCustomSound;
import me.udnek.coreu.custom.sound.CustomSound;
import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoGeneratingFilesSound implements CustomComponent<CustomSound> {

    public static final AutoGeneratingFilesSound DEFAULT = new AutoGeneratingFilesSound();

    public @NotNull List<VirtualRpJsonFile> getFiles(@NotNull CustomSound unknowSound){
        if (!(unknowSound instanceof ConstructableCustomSound sound)) return List.of();
        VirtualRpJsonFile file = new VirtualRpJsonFile(JsonParser.parseString("""
                    {
                        "%id%": {
                            "subtitle": "%subtitle%",
                            "sounds": [%sounds%],
                            "type": "event"
                        }
                    }
                    """
                .replace("%id%", sound.key().value())
                .replace("%subtitle%", sound.translationKey())
                .replace("%sounds%", String.join(", ", sound.getFilePaths().stream().map(s -> "\""+s+"\"").toList()))
        ), "assets/" + sound.key().namespace() + "/sounds.json");
        return List.of(file);
    }


    @Override
    public @NotNull CustomComponentType<? super CustomSound, ? extends CustomComponent<? super CustomSound>> getType(){
        return CustomComponentType.AUTO_GENERATING_FILES_SOUND;
    }
}