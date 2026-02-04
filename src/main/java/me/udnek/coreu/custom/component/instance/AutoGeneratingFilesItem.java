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
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

@org.jspecify.annotations.NullMarked public  interface AutoGeneratingFilesItem extends CustomComponent<CustomItem>{

    Generated GENERATED = new Generated();
    HandHeld HANDHELD = new HandHeld();
    Bow BOW = new Bow();
    Crossbow CROSSBOW = new Crossbow();
    CustomModelDataColorable CUSTOM_MODEL_DATA_COLORABLE = new CustomModelDataColorable();
    DyeColorable DYE_COLORABLE = new DyeColorable();
    Generated20x20 GENERATED_20X20 = new Generated20x20();
    Bow20x20 BOW_20X20 = new Bow20x20();
    Handheld20x20 HANDHELD_20X20 = new Handheld20x20();
    TwoLayered TWO_LAYERED = new TwoLayered();
    Compass COMPASS_SINGLE_LAYER = new Compass(false);
    Compass COMPASS_TWO_LAYERS = new Compass(true);

    List<VirtualRpJsonFile> getFiles(CustomItem customItem);

    @Override
    default CustomComponentType<CustomItem, ? extends CustomComponent<CustomItem>> getType(){
        return CustomComponentType.AUTO_GENERATING_FILES_ITEM;
    }

    abstract class Base implements AutoGeneratingFilesItem{

        public boolean isOversizedInGui(){return true;}
        public boolean isHandAnimationOnSwap(){return true;}
        public float swapAnimationScale(){return 1.0f;}

        @Override
        public List<VirtualRpJsonFile> getFiles(CustomItem customItem){
            Key itemModel = customItem.getItem().getData(DataComponentTypes.ITEM_MODEL);
            if (itemModel == null || itemModel.namespace().equals(Key.MINECRAFT_NAMESPACE)) return List.of();
            return getFiles(itemModel);
        }

        public ArrayList<VirtualRpJsonFile> getFiles(Key itemModel){
            ArrayList<VirtualRpJsonFile> files = new ArrayList<>();
            files.add(getDefinitionFile(itemModel));
            files.addAll(getModelsFiles(itemModel));
            return files;
        }

        public String getModelPath(Key itemModel){
            return "assets/" + itemModel.namespace() + "/models/item/" + itemModel.value() + ".json";
        }
        public String getDefinitionPath(Key itemModel){
            return "assets/" + itemModel.namespace() + "/items/" + itemModel.value() + ".json";
        }
        public List<VirtualRpJsonFile> getModelsFiles(Key itemModel){
            List<VirtualRpJsonFile> files = new ArrayList<>();
            for (Pair<Key, JsonObject> keyAndModel : getModels(itemModel)) {
                files.add(new VirtualRpJsonFile(keyAndModel.getRight(), getModelPath(keyAndModel.getLeft())));
            }
            return files;
        }
        public VirtualRpJsonFile getDefinitionFile(Key itemModel){
            return new VirtualRpJsonFile(getDefinition(itemModel), getDefinitionPath(itemModel));
        }
        public String replacePlaceHolders(String data, Key itemModel){
            return data
                    .replace("%namespace%", itemModel.namespace())
                    .replace("%key%", itemModel.value())
                    .replace("%texture_path%", itemModel.namespace()+":item/"+itemModel.value())
                    .replace("%model_path%", itemModel.namespace()+":item/"+itemModel.value())
                    .replace("%hand_animation_on_swap%", String.valueOf(isHandAnimationOnSwap()))
                    .replace("%oversized_in_gui%", String.valueOf(isOversizedInGui()))
                    .replace("%swap_animation_scale%", String.valueOf(swapAnimationScale()));
        }

        public abstract List<Pair<Key, JsonObject>> getModels(Key modelKey);
        public JsonObject getDefinition(Key modelKey){
            return (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                        {
                            "model": {
                                "type": "minecraft:model",
                                "model": "%model_path%"
                            },
                            "oversized_in_gui": %oversized_in_gui%,
                            "hand_animation_on_swap": %hand_animation_on_swap%,
                            "swap_animation_scale": %swap_animation_scale%
                        }
                        """, modelKey));
        }
    }
    class Generated extends Base {
        @Override
        public List<Pair<Key, JsonObject>> getModels(Key modelKey) {
            return List.of(Pair.of(modelKey, generateModel(modelKey)));
        }

        public JsonObject generateModel(Key modelKey){
            return (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                        {
                            "parent": "minecraft:item/generated",
                            "textures": {
                                "layer0": "%texture_path%"
                            }
                        }
                        """, modelKey));
        }
    }
    class TwoLayered extends Generated{
        public JsonObject generateModel(Key modelKey){
            return (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                        {
                            "parent": "minecraft:item/generated",
                            "textures": {
                                "layer0": "%texture_path%_base",
                                "layer1": "%texture_path%_overlay"
                            }
                        }
                        """, modelKey));
        }
    }
    class HandHeld extends Generated{
        @Override
        public JsonObject generateModel(Key modelKey) {
            JsonObject model = super.generateModel(modelKey);
            model.addProperty("parent", "minecraft:item/handheld");
            return model;
        }
    }
    class CustomModelDataColorable extends Generated{
        @Override
        public JsonObject getDefinition(Key itemModel) {
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
        public JsonObject getDefinition(Key itemModel) {
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
        public JsonObject getDefinition(Key itemModel) {
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
                      },
                        "oversized_in_gui": %oversized_in_gui%,
                        "hand_animation_on_swap": %hand_animation_on_swap%,
                        "swap_animation_scale": %swap_animation_scale%
                    }""", itemModel));
        }

        @Override
        public JsonObject generateModel(Key modelKey) {
            JsonObject model = super.generateModel(modelKey);
            model.addProperty("parent", "minecraft:item/bow");
            return model;
        }

        @Override
        public List<Pair<Key, JsonObject>> getModels(Key modelKey) {
            ArrayList<Pair<Key, JsonObject>> models = new ArrayList<>();
            models.add(Pair.of(modelKey, generateModel(modelKey)));
            for (int i = 0; i < 3; i++) {
                NamespacedKey key = new NamespacedKey(modelKey.namespace(), modelKey.value() + "_pulling_" + i);
                models.add(Pair.of(key, generateModel(key)));
            }
            return models;
        }

    }
    class Crossbow extends Bow{
        @Override
        public JsonObject getDefinition(Key itemModel) {
            return  (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                    {
                    "model": {
                       "type": "minecraft:select",
                       "cases": [
                         {
                           "model": {
                             "type": "minecraft:model",
                             "model": "%model_path%_arrow"
                           },
                           "when": "arrow"
                         },
                         {
                           "model": {
                             "type": "minecraft:model",
                             "model": "%model_path%_firework"
                           },
                           "when": "rocket"
                         }
                       ],
                       "fallback": {
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
                               "threshold": 0.58
                             },
                             {
                               "model": {
                                 "type": "minecraft:model",
                                 "model": "%model_path%_pulling_2"
                               },
                               "threshold": 1.0
                             }
                           ],
                           "fallback": {
                             "type": "minecraft:model",
                                 "model": "%model_path%_pulling_0"
                           },
                           "property": "minecraft:crossbow/pull"
                         },
                         "property": "minecraft:using_item"
                       },
                       "property": "minecraft:charge_type"
                     },
                        "oversized_in_gui": %oversized_in_gui%,
                        "hand_animation_on_swap": %hand_animation_on_swap%,
                        "swap_animation_scale": %swap_animation_scale%
                    }""", itemModel));
        }

        @Override
        public JsonObject generateModel(Key modelKey) {
            JsonObject model = super.generateModel(modelKey);
            model.addProperty("parent", "minecraft:item/crossbow");
            return model;
        }


        @Override
        public List<Pair<Key, JsonObject>> getModels(Key modelKey) {
            List<Pair<Key, JsonObject>> models = super.getModels(modelKey);
            {
                NamespacedKey key = new NamespacedKey(modelKey.namespace(), modelKey.value() + "_arrow");
                models.add(Pair.of(key, generateModel(key)));
            }
            {
                NamespacedKey key = new NamespacedKey(modelKey.namespace(), modelKey.value() + "_firework");
                models.add(Pair.of(key, generateModel(key)));
            }
            return models;
        }
    }
    class Generated20x20 extends Generated{
        @Override
        public JsonObject generateModel(Key modelKey) {
            JsonObject model = super.generateModel(modelKey);
            model.addProperty("parent", CoreU.getKey("item/generated_20x20").toString());
            return model;
        }
    }
    class Bow20x20 extends Bow{
        @Override
        public JsonObject generateModel(Key modelKey) {
            JsonObject model = super.generateModel(modelKey);
            model.addProperty("parent", CoreU.getKey("item/bow_20x20").toString());
            return model;
        }
    }
    class Handheld20x20 extends Generated20x20{
        @Override
        public JsonObject generateModel(Key modelKey) {
            JsonObject model = super.generateModel(modelKey);
            model.addProperty("parent", CoreU.getKey("item/handheld_20x20").toString());
            return model;
        }
    }
    class Compass extends Generated{

        private final boolean twoLayered;

        public Compass(boolean twoLayered){
            this.twoLayered = twoLayered;
        }

        public String rawCompassDefinition(){
            return """
                    {
                    "model": {
                      "type": "minecraft:condition",
                      "component": "minecraft:lodestone_tracker",
                      "on_false": {
                        "type": "minecraft:range_dispatch",
                        "entries": [
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_16"
                            },
                            "threshold": 0.0
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_17"
                            },
                            "threshold": 0.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_18"
                            },
                            "threshold": 1.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_19"
                            },
                            "threshold": 2.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_20"
                            },
                            "threshold": 3.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_21"
                            },
                            "threshold": 4.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_22"
                            },
                            "threshold": 5.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_23"
                            },
                            "threshold": 6.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_24"
                            },
                            "threshold": 7.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_25"
                            },
                            "threshold": 8.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_26"
                            },
                            "threshold": 9.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_27"
                            },
                            "threshold": 10.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_28"
                            },
                            "threshold": 11.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_29"
                            },
                            "threshold": 12.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_30"
                            },
                            "threshold": 13.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_31"
                            },
                            "threshold": 14.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_00"
                            },
                            "threshold": 15.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_01"
                            },
                            "threshold": 16.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_02"
                            },
                            "threshold": 17.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_03"
                            },
                            "threshold": 18.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_04"
                            },
                            "threshold": 19.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_05"
                            },
                            "threshold": 20.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_06"
                            },
                            "threshold": 21.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_07"
                            },
                            "threshold": 22.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_08"
                            },
                            "threshold": 23.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_09"
                            },
                            "threshold": 24.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_10"
                            },
                            "threshold": 25.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_11"
                            },
                            "threshold": 26.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_12"
                            },
                            "threshold": 27.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_13"
                            },
                            "threshold": 28.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_14"
                            },
                            "threshold": 29.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_15"
                            },
                            "threshold": 30.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_16"
                            },
                            "threshold": 31.5
                          }
                        ],
                        "property": "minecraft:compass",
                        "scale": 32.0,
                        "target": "spawn"
                      },
                      "on_true": {
                        "type": "minecraft:range_dispatch",
                        "entries": [
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_16"
                            },
                            "threshold": 0.0
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_17"
                            },
                            "threshold": 0.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_18"
                            },
                            "threshold": 1.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_19"
                            },
                            "threshold": 2.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_20"
                            },
                            "threshold": 3.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_21"
                            },
                            "threshold": 4.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_22"
                            },
                            "threshold": 5.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_23"
                            },
                            "threshold": 6.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_24"
                            },
                            "threshold": 7.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_25"
                            },
                            "threshold": 8.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_26"
                            },
                            "threshold": 9.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_27"
                            },
                            "threshold": 10.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_28"
                            },
                            "threshold": 11.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_29"
                            },
                            "threshold": 12.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_30"
                            },
                            "threshold": 13.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_31"
                            },
                            "threshold": 14.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_00"
                            },
                            "threshold": 15.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_01"
                            },
                            "threshold": 16.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_02"
                            },
                            "threshold": 17.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_03"
                            },
                            "threshold": 18.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_04"
                            },
                            "threshold": 19.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_05"
                            },
                            "threshold": 20.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_06"
                            },
                            "threshold": 21.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_07"
                            },
                            "threshold": 22.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_08"
                            },
                            "threshold": 23.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_09"
                            },
                            "threshold": 24.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_10"
                            },
                            "threshold": 25.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_11"
                            },
                            "threshold": 26.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_12"
                            },
                            "threshold": 27.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_13"
                            },
                            "threshold": 28.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_14"
                            },
                            "threshold": 29.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_15"
                            },
                            "threshold": 30.5
                          },
                          {
                            "model": {
                              "type": "minecraft:model",
                              "model": "minecraft:item/compass_16"
                            },
                            "threshold": 31.5
                          }
                        ],
                        "property": "minecraft:compass",
                        "scale": 32.0,
                        "target": "lodestone"
                      },
                      "property": "minecraft:has_component"
                    },
                      "oversized_in_gui": %oversized_in_gui%,
                      "hand_animation_on_swap": %hand_animation_on_swap%,
                        "swap_animation_scale": %swap_animation_scale%
                    }
                    """;
        }

        @Override
        public JsonObject getDefinition(Key itemModel) {
            String rawDefinition = rawCompassDefinition()
                    .replace(   "\"model\": \"minecraft:item/compass_",
                            "\"model\": \"%namespace%:item/%key%/"
                    );
            return (JsonObject) JsonParser.parseString(replacePlaceHolders(rawDefinition, itemModel));
        }

        public Pair<Key, JsonObject> generateTwoLayeredModel(Key baseModel, String n){
            NamespacedKey localModel = new NamespacedKey(baseModel.namespace(), baseModel.value() + "/" + n);
            return Pair.of(localModel, (JsonObject) JsonParser.parseString(replacePlaceHolders("""
                        {
                            "parent": "minecraft:item/generated",
                            "textures": {
                                "layer0": "%texture_path%/base",
                                "layer1": "%texture_path%/%n%"
                            }
                        }
                        """.replace("%n%", n), baseModel)));
        }

        public Pair<Key, JsonObject> generateSingleLayeredModel(Key modelKey, String n){
            NamespacedKey newKey = new NamespacedKey(modelKey.namespace(), modelKey.value() + "/" + n);
            return Pair.of(newKey, generateModel(newKey));
        }

        @Override
        public List<Pair<Key, JsonObject>> getModels(Key modelKey) {
            List<Pair<Key, JsonObject>> all = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                String n = String.format("%02d", i);
                if (twoLayered){
                    all.add(generateTwoLayeredModel(modelKey, n));
                } else {
                    all.add(generateSingleLayeredModel(modelKey, n));
                }
            }
            return all;
        }
    }
}























