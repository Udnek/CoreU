package me.udnek.coreu.custom.component.instance;

import com.google.gson.JsonParser;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.LoreProvidingItemComponent;
import me.udnek.coreu.custom.sound.ConstructableCustomSound;
import me.udnek.coreu.custom.sound.CustomSound;
import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AutoGeneratingFilesSound extends LoreProvidingItemComponent {

    @NotNull List<VirtualRpJsonFile> getFiles(@NotNull CustomSound sound);

    @Override
    @NotNull
    default CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.AUTO_GENERATING_FILES_ITEM;
    }

    class Instance implements AutoGeneratingFilesSound{

        @Override
        public @NotNull List<VirtualRpJsonFile> getFiles(@NotNull CustomSound sound) {
            if (!(sound instanceof ConstructableCustomSound customSound)) return List.of();
            VirtualRpJsonFile file = new VirtualRpJsonFile(JsonParser.parseString("""
                    {
                        "%id%": {
                            "subtitle": "%sub%",
                            "sounds": [%sounds%],
                            "type": "event"
                        }
                    }
                    """
                    .replace("%id%", sound.key().value())
                    .replace("%sub%", sound.getSubtitle())
                    .replace("%sounds%", )
            ), "assets/" + sound.key().namespace() + "/sound.json");
            return List.of(file);
        }
        public @NotNull VirtualRpJsonFile getSoundsFile(@NotNull CustomSound sound){

        }

    }
}