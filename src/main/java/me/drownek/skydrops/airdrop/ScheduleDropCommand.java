package me.drownek.skydrops.airdrop;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.util.localization.LocalizationManager;
import me.drownek.util.localization.MessageKey;
import me.drownek.util.message.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command(name = "skydrops")
@Permission("skydrops.command.scheduledrop")
public class ScheduleDropCommand {

    private @Inject AirdropTask airdropTask;

    @Execute(name = "scheduledrop")
    void scheduleDrop(@Context Player player, @Arg Location location) {
        location.setWorld(player.getWorld());
        airdropTask.setLastDropTime(0);
        airdropTask.setNextLocation(location);
        TextUtil.message(player, LocalizationManager.getMessage(MessageKey.COMMAND_UTIL_SUCCESS_MESSAGE));
    }
}
