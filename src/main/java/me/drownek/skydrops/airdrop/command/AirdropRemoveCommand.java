package me.drownek.skydrops.airdrop.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.injector.annotation.Inject;
import me.drownek.skydrops.airdrop.AirdropService;
import me.drownek.util.localization.MessageKey;
import org.bukkit.entity.Player;

@Command(name = "skydrops")
@Permission("skydrops.command.removedrops")
public class AirdropRemoveCommand {

    private @Inject AirdropService airdropService;

    @Execute(name = "removedrops")
    void removeDrops(@Context Player player) {
        airdropService.removeAirdrops();
        MessageKey.COMMAND_SUCCESS.send(player);
    }
}
