package com.obqy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {
    public static Config INSTANCE = new Config();

    public double musicVolume = -1.0;

    private static final File FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("MusicToggle.json")
            .toFile();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(FILE)) {
            INSTANCE = GSON.fromJson(reader, Config.class);
        } catch (IOException e) {
            MusicToggle.LOGGER.warn("[MusicToggle] Failed to load config, non fatal error");
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            MusicToggle.LOGGER.error("[MusicToggle] Failed to save config: {}", e.getMessage());
        }
    }
}
