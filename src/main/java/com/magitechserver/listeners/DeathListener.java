package com.magitechserver.listeners;

import com.magitechserver.DeathPunishment;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.statistic.Statistic;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Frani on 13/07/2017.
 */
public class DeathListener {

    public EconomyService eco;

    @Listener
    public void onDeath(DestructEntityEvent.Death e) {
        if(e.getTargetEntity() instanceof Player) {
            Player player = (Player) e.getTargetEntity();
            int price = DeathPunishment.getConfig().getInt("misc", "death-cost");
            Map<Statistic, Long> stats = player.getOrNull(Keys.STATISTICS);

            Integer deathCount = stats.entrySet().stream().filter(p ->
                p.getKey().getId().toLowerCase().contains("deaths")
            ).map(Map.Entry::getValue).findFirst().orElse(1L).intValue();

            if(DeathPunishment.getConfig().getIMap("counter").containsKey(deathCount)) {
                String[] commands = DeathPunishment.getConfig().getIMap("counter").get(deathCount).replace("%player%", player.getName()).split(";");

                if(commands.length == 1) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), DeathPunishment.getConfig().getIMap("counter").get(deathCount));
                } else {
                    for (String command : commands) {
                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
                    }
                }
            }

            if(price > 0 && Sponge.getServiceManager().isRegistered(EconomyService.class)) {
                EconomyService eco = Sponge.getServiceManager().provide(EconomyService.class).get();
                Optional<UniqueAccount> optionalAccount = eco.getOrCreateAccount(player.getUniqueId());
                if(optionalAccount.isPresent()) {
                    UniqueAccount account = optionalAccount.get();
                    if(account.getBalance(eco.getDefaultCurrency()).compareTo(BigDecimal.valueOf(price)) < 0) {
                        return;
                    } else {
                        account.withdraw(eco.getDefaultCurrency(), BigDecimal.valueOf(price), Cause.source(DeathPunishment.getInstance()).build());
                    }
                }
            }
        }
    }

}
