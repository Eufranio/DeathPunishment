package com.magitechserver;

import com.google.inject.Inject;
import com.magitechserver.config.Config;
import com.magitechserver.listeners.DeathListener;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;

/**
 * Created by Frani on 13/07/2017.
 */

@Plugin(id = "deathpunishment",
        name = "DeathPunishment",
        description = "A Sponge plugin that applies punishments to players who die in the game",
        authors = { "Eufranio" })

public class DeathPunishment {

    private static DeathPunishment instance = null;
    private Config config;
    public static Logger logger;
    public static DeathListener deathListener;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configFile;

    @Inject
    public DeathPunishment(Logger logger) {
        this.logger = logger;
    }

    public static Config getConfig() {
        return instance.config;
    }

    public static DeathPunishment getInstance() {
        return instance;
    }

    @Listener
    public void init(GameInitializationEvent e) {
        instance = this;
        deathListener = new DeathListener();
        initStuff(false);
    }

    @Listener
    public void stop(GameStoppingEvent e) {
        stopStuff(false);
    }

    @Listener
    public void reload(GameReloadEvent e) {
        stopStuff(true);
        initStuff(true);
        logger.info("DeathPunishment was reloaded successfully!");
    }


    private void initStuff(Boolean reloading) {
        logger.info("DeathPunishment is starting!");
        config = new Config(this, configFile);
        config.load();
        Sponge.getEventManager().registerListeners(this, deathListener);
    }

    private void stopStuff(Boolean reloading) {
        logger.info("DeathPunishment is stopping!");
        Sponge.getEventManager().unregisterListeners(deathListener);
        config = null;
    }


}
