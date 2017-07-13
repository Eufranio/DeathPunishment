package com.magitechserver.config;

import com.google.common.reflect.TypeToken;
import com.magitechserver.DeathPunishment;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.economy.EconomyService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by Frani on 13/07/2017.
 */
public class Config {

    private final DeathPunishment instance;
    private final Path configFile;
    private ConfigurationNode node;
    private HoconConfigurationLoader loader;
    private TypeToken<Map<String, String>> typeToken;
    private TypeToken<Map<Integer, String>> typeTokenS;

    public Config(DeathPunishment instance, Path configFile) {
        this.instance = instance;
        this.configFile = configFile;
    }

    public void load() {
        if(!configFile.toFile().exists()) {
            try {
                Sponge.getAssetManager().getAsset(instance, "DeathPunishment.conf").get().copyToFile(configFile);
            } catch (Exception e) {
                DeathPunishment.logger.error("Could not copy the default config, aborting!");
                e.printStackTrace();
                return;
            }
        }

        loader = HoconConfigurationLoader.builder().setPath(configFile).build();

        try {
            node = loader.load();
        } catch (IOException e) {
            DeathPunishment.logger.error("Could not load the config! Stacktrace: ");
            e.printStackTrace();
            return;
        }
        typeToken = new TypeToken<Map<String, String>>() {};
        typeTokenS = new TypeToken<Map<Integer, String>>() {};

        if(!Sponge.getServiceManager().isRegistered(EconomyService.class) && DeathPunishment.getConfig().getInt("misc", "death-cost") > 0) {
            DeathPunishment.logger.error("There is no economy plugin available, ignoring death cost!");
        }

        DeathPunishment.logger.info("Config loaded successfully!");
    }

    public Boolean getBool(Object... key) {
        return node.getNode(key).getBoolean(false);
    }

    public String getString(Object... key) {
        return node.getNode(key).getString();
    }

    public int getInt(Object... key) {
        return node.getNode(key).getInt();
    }

    public Map<String, String> getSMap(Object... key) {
        try {
            return node.getNode(key).getValue(typeToken);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<Integer, String> getIMap(Object... key) {
        try {
            return node.getNode(key).getValue(typeTokenS);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return null;
    }



}
