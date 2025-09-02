package me.drownek.skydrops.airdrop;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import eu.okaeri.injector.annotation.Inject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command(name = "skydrops")
@Permission("skydrops.command.spawndrop")
public class AirdropSpawnCommand {

    private @Inject AirdropService airdropService;

    @Execute(name = "spawndrop")
    void spawnDrop(@Context Player player, @Arg Location location) {
        location.setWorld(player.getWorld());
        airdropService.createAirdrop(location);
    }
}
