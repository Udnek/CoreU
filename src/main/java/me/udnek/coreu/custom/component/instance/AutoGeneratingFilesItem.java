package me.udnek.coreu.custom.component.instance;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface AutoGeneratingFilesItem extends CustomComponent<CustomItem> {

    Generated GENERATED = new Generated();
    HandHeld HANDHELD = new HandHeld();
    Bow BOW = new Bow();
    CustomModelDataColorable CUSTOM_MODEL_DATA_COLORABLE = new CustomModelDataColorable();
    DyeColorable DYE_COLORABLE = new DyeColorable();
    Generated20x20 GENERATED_20X20 = new Generated20x20();
    Bow20x20 BOW_20X20 = new Bow20x20();
    Handheld20x20 HANDHELD_20X20 = new Handheld20x20();

    @NotNull List<VirtualRpJsonFile> getFiles(@NotNull CustomItem customItem);

    @Override
    @NotNull
    default CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType(){
        return CustomComponentType.AUTO_GENERATING_FILES_ITEM;
    }

    interface Base extends AutoGeneratingFilesItem{

        default boolean isOversizedInGui(){return true;}
        default boolean isHandAnimationOnSwap(){return true;}

        @Override
        @NotNull
        default List<VirtualRpJsonFile> getFiles(@NotNull CustomItem customItem){
            Key itemModel = customItem.getItem().getData(DataComponentTypes.ITEM_MODEL);
            if (itemModel == null || itemModel.namespace().equals(Key.MINECRAFT_NAMESPACE)) return List.of();
            return getFiles(itemModel);
        }

        default @NotNull ArrayList<VirtualRpJsonFile> getFiles(@NotNull Key itemModel){
            ArrayList<VirtualRpJsonFile> files = new ArrayList<>();
            files.add(getDefinitionFile(itemModel));
            files.addAll(getModelsFiles(itemModel));
            return files;
        }

        default @NotNull String getModelPath(@NotNull Key itemModel){
            return "assets/" + itemModel.namespace() + "/models/item/" + itemModel.value() + ".json";
        }
        default @NotNull String getDefinitionPath(@NotNull Key itemModel){
            return "assets/" + itemModel.namespace() + "/items/" + itemModel.value() + ".json";
        }
        default @NotNull List<VirtualRpJsonFile> getModelsFiles(@NotNull Key itemModel){
            List<VirtualRpJsonFile> files = new ArrayList<>();
            String modelPath = getModelPath(itemModel);
            getModels(itemModel).forEach(model -> files.add(new VirtualRpJsonFile(model, modelPath)));
            return files;
        }
        default @NotNull VirtualRpJsonFile getDefinitionFile(@NotNull Key itemModel){
            return new VirtualRpJsonFile(getDefinition(itemModel), getDefinitionPath(itemModel));
        }
        @NotNull
        default String replacePlaceHolders(@NotNull String data, @NotNull Key itemModel){
            return data
                    .replace("%namespace%", itemModel.namespace())
                    .replace("%key%", itemModel.value())
                    .replace("%texture_path%", itemModel.namespace()+":item/"+itemModel.value())
                    .replace("%model_path%", itemModel.namespace()+":item/"+itemModel.value())
                    .replace("%hand_animation_on_swap%", String.valueOf(isHandAnimationOnSwap()))
                    .replace("%oversized_in_gui%", String.valueOf(isOversizedInGui()));
        }

        @NotNull List<JsonObject> getModels(@NotNull Key modelKey);
        @NotNull JsonObject getDefinition(@NotNull Key modelKey);
    }
    class Generated implements Base{
        @Override
        public @NotNull List<JsonObject> getModels(@NotNull Key itemModel) {
            return List.of((JsonObject) JsonParser.parseString(replacePlaceHolders("""
                        {
                            "parent": "minecraft:item/generated",
                            "textures": {
                                "layer0": "%texture_path%"
                            }
                        }
                        """, itemModel)));
        }
        @Override
        public @NotNull JsonObject getDefinition(@NotNull Key itemModel) {
            return (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                        {
                            "model": {
                                "type": "minecraft:model",
                                "model": "%model_path%"
                            },
                            "oversized_in_gui": %oversized_in_gui%,
                            "hand_animation_on_swap%": %hand_animation_on_swap%
                        }
                        """, itemModel));
        }
    }
    class HandHeld extends Generated{

        @Override
        public @NotNull List<JsonObject> getModels(@NotNull Key itemModel) {
            @NotNull List<JsonObject> models = super.getModels(itemModel);
            models.getFirst().addProperty("parent", "minecraft:item/handheld");
            return models;
        }
    }
    class CustomModelDataColorable extends Generated{
        @Override
        public @NotNull JsonObject getDefinition(@NotNull Key itemModel) {
            JsonObject definition = super.getDefinition(itemModel);
            JsonElement tints = JsonParser.parseString(
                    """
                            {
                            "tints": [
                                  {
                                    "type": "minecraft:custom_model_data",
                                    "default": 0
                                  }
                            ]
                            }
                            """).getAsJsonObject().get("tints");
            definition.get("model").getAsJsonObject().add("tints", tints);
            return definition;
        }
    }
    class DyeColorable extends Generated{
        @Override
        public @NotNull JsonObject getDefinition(@NotNull Key itemModel) {
            JsonObject definition = super.getDefinition(itemModel);
            JsonElement tints = JsonParser.parseString(
                    """
                            {
                            "tints": [
                                  {
                                    "type": "minecraft:dye",
                                    "default": 0
                                  }
                            ]
                            }
                            """).getAsJsonObject().get("tints");
            definition.get("model").getAsJsonObject().add("tints", tints);
            return definition;
        }
    }

    class Bow extends Generated{

        @Override
        public @NotNull JsonObject getDefinition(@NotNull Key itemModel) {
            return (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                    {
                      "model": {
                        "type": "minecraft:condition",
                        "on_false": {
                          "type": "minecraft:model",
                          "model": "%model_path%"
                        },
                        "on_true": {
                          "type": "minecraft:range_dispatch",
                          "entries": [
                            {
                              "model": {
                                "type": "minecraft:model",
                                "model": "%model_path%_pulling_1"
                              },
                              "threshold": 0.65
                            },
                            {
                              "model": {
                                "type": "minecraft:model",
                                "model": "%model_path%_pulling_2"
                              },
                              "threshold": 0.9
                            }
                          ],
                          "fallback": {
                            "type": "minecraft:model",
                            "model": "%model_path%_pulling_0"
                          },
                          "property": "minecraft:use_duration",
                          "scale": 0.05
                        },
                        "property": "minecraft:using_item"
                      }
                    }""", itemModel));
        }

        @Override
        public @NotNull List<JsonObject> getModels(@NotNull Key itemModel) {
            @NotNull List<JsonObject> models = new ArrayList<>(super.getModels(itemModel));
            for (int i = 0; i < 3; i++) {
                models.addAll(super.getModels(new NamespacedKey(itemModel.namespace(), itemModel.value() + "_pulling_" + i)));
            }
            for (JsonObject model : models) {
                model.addProperty("parent", "minecraft:item/bow");
            }
            return models;
        }

    }
    class Generated20x20 extends Generated{
        @Override
        public @NotNull List<JsonObject> getModels(@NotNull Key itemModel) {
            @NotNull List<JsonObject> models = super.getModels(itemModel);
            models.getFirst().addProperty("parent", CoreU.getKey("item/generated_20x20").toString());
            return models;
        }
    }
    class Bow20x20 extends Bow{
        @Override
        public @NotNull List<JsonObject> getModels(@NotNull Key itemModel) {
            @NotNull List<JsonObject> models = super.getModels(itemModel);
            models.getFirst().addProperty("parent", CoreU.getKey("item/bow_20x20").toString());
            return models;
        }
    }
    class Handheld20x20 extends Generated20x20{
        @Override
        public @NotNull List<JsonObject> getModels(@NotNull Key itemModel) {
            @NotNull List<JsonObject> model = super.getModels(itemModel);
            model.getFirst().addProperty("parent", CoreU.getKey("item/handheld_20x20").toString());
            return model;
        }
    }
}























