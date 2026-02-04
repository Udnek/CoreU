package me.udnek.coreu.resourcepack.merger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.jspecify.annotations.NullMarked public class MapLikeMerger implements RpFileMerger{
    private final List<JsonObject> jsons = new ArrayList<>();
    private JsonObject merged = null;

    public MapLikeMerger(){}
    @Override
    public void add(JsonObject jsonObject){
        jsons.add(jsonObject);
    }

    @Override
    public void merge(){
        merged = new JsonObject();
        for (JsonObject json : jsons) {
            for (Map.Entry<String, JsonElement> entry : json.asMap().entrySet()) {
                merged.add(entry.getKey(), entry.getValue());
            }
        }
    }


    @Override
    public String getMergedAsString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(merged.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String prettyJson;
        try {
            prettyJson = objectMapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return prettyJson;
    }
}
