package me.drownek.skydrops.settings;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.datagatherer.DataGatherer;
import me.drownek.datagatherer.StepResult;
import me.drownek.datagatherer.step.MsgStep;
import me.drownek.datagatherer.step.Step;
import me.drownek.util.TimeUtil;
import me.drownek.util.message.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.time.Duration;

@Command(name = "skydrops")
@Permission("skydrops.ingame.settings")
public class InGameSettingsCommand {

    private @Inject InGameSettingsGui gui;
    private @Inject Plugin plugin;

    @Execute(name = "settings")
    void settings(@Context Player player) {
        gui.open(player);
    }
}
