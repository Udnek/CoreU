package me.udnek.coreu.resourcepack.merger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;

@org.jspecify.annotations.NullMarked public  interface RpFileMerger{
    void add(JsonObject jsonObject);
    default void add(BufferedReader reader){
       add((JsonObject) JsonParser.parseReader(reader));
    }
    void merge();
    String getMergedAsString();
}
