package me.udnek.coreu.custom.component.instance;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.registry.Registrable;
import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import net.kyori.adventure.translation.Translatable;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@org.jspecify.annotations.NullMarked public class TranslatableThing implements CustomComponent<Object>{

    public static final TranslatableThing DEFAULT = new TranslatableThing(null)
        {
            @Override
            public TranslatableThing addAdditional(String key, Translations translations) {
                throwCanNotChangeDefault();
                return this;
            }

            @Override
            public List<VirtualRpJsonFile> getFiles(Translatable translatable, Registrable registrable) {
                return List.of();
            }
        };

    public static final String EN_US = "en_us";
    public static final String RU_RU = "ru_ru";

    protected Map<String, Translations> additionalSuffixesTranslations = Map.of();
    public final Translations main;

    public static TranslatableThing ofEngAndRu(String eng, String ru){
        return new TranslatableThing(Translations.ofEngAndRu(eng, ru));
    }

    public static TranslatableThing ofEng(String eng){
        return new TranslatableThing(Translations.ofEng(eng));
    }

    public TranslatableThing(@Nullable TranslatableThing.Translations main){
        if (main == null) main = Translations.EMPTY;
        this.main = main;
    }

    public List<VirtualRpJsonFile> getFiles(Translatable translatable, Registrable registrable){
        Set<String> langs = new HashSet<>(main.langToTranslation.keySet());
        additionalSuffixesTranslations.forEach((string, translations) -> langs.addAll(translations.langToTranslation.keySet()));
        List<VirtualRpJsonFile> files = new ArrayList<>();
        for (String lang : langs) {
            JsonObject json = new JsonObject();
            String mainTrans = main.getByLang(lang);
            if (mainTrans != null){
                json.add(translatable.translationKey(), new JsonPrimitive(mainTrans));
            }
            for (Map.Entry<String, Translations> entry : additionalSuffixesTranslations.entrySet()) {
                String trans = entry.getValue().getByLang(lang);
                if (trans == null) continue;
                json.add(translatable.translationKey() + "." + entry.getKey(), new JsonPrimitive(trans));
            }
            files.add(new VirtualRpJsonFile(json, "assets/"+registrable.key().namespace()+"/lang/"+lang+".json"));
        }
        return files;
    }

    public TranslatableThing addAdditional(String key, Translations translations){
        if (additionalSuffixesTranslations.isEmpty()) additionalSuffixesTranslations = new HashMap<>();
        additionalSuffixesTranslations.put(key, translations);
        return this;
    }

    @Override
    public CustomComponentType<Object, ? extends CustomComponent<Object>> getType() {
        return CustomComponentType.TRANSLATABLE_THING;
    }

    public static class Translations{

        private Map<String, String> langToTranslation;

        public static final Translations EMPTY = new Translations(Map.of()){
            @Override
            public void addLang(String lang, String translation) {
                throwCanNotChangeDefault();
            }
        };

        public static Translations ofEngAndRu(String eng, String ru){
            return new Translations(Map.of(EN_US, eng, RU_RU, ru));
        }

        public static Translations ofEng(String eng){
            return new Translations(Map.of(EN_US, eng));
        }

        public Translations(Map<String, String> langToTranslation){
            this.langToTranslation = langToTranslation;
        }

        public void addLang(String lang, String translation){
            if (!(langToTranslation instanceof HashMap<String, String>)) langToTranslation = new HashMap<>(langToTranslation);
            langToTranslation.put(lang, translation);
        }

        public @Nullable String getByLang(String lang){
            return langToTranslation.getOrDefault(lang, null);
        }

        void throwCanNotChangeDefault(){
            throw new RuntimeException("Can not change default Translations: create default or apply new firstly");
        }
    }
}



















