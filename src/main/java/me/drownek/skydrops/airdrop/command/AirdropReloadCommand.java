package me.drownek.skydrops.airdrop.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.injector.Injector;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.skydrops.lang.LangConfig;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "skydrops")
@Permission("skydrops.command.reload")
public class AirdropReloadCommand {

    private @Inject Injector injector;
    private @Inject LangConfig langConfig;
    private @Inject Logger logger;

    @Execute(name = "reload")
    void reload(@Context CommandSender commandSender) {
        try {
            injector.streamOf(OkaeriConfig.class).forEach(OkaeriConfig::load);
            langConfig.reloadConfigSuccess.sendTo(commandSender);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Configuration reload failed, continuing with previous settings", e);
            langConfig.reloadConfigError.sendTo(commandSender);
        }
    }
}
