package me.drownek.skydrops.drop.items;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.exception.SignGUIVersionException;
import dev.rollczi.litecommands.annotations.async.Async;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.bukkit.scheduler.PlatformScheduler;
import me.drownek.skydrops.SkyDropsPlugin;
import me.drownek.skydrops.lang.LangConfig;
import me.drownek.util.ItemStackBuilder;
import me.drownek.util.message.Formatter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Command(name = "skydrops")
@Permission("skydrops.edit.drops")
public class DropEditorCommand {

    private @Inject DropConfig dropConfig;
    private @Inject LangConfig langConfig;
    private @Inject DropService dropService;
    private @Inject SkyDropsPlugin plugin;
    private @Inject PlatformScheduler scheduler;

    @Async
    @Execute(name = "edit-drops")
    void editDrops(@Context Player player) {
        Gui gui = Gui.gui()
            .title(Component.text(langConfig.editDropGuiTitle))
            .rows(6)
            .disableAllInteractions()
            .create();

        dropConfig.dropItems.forEach(dropItem ->
            gui.addItem(createDropGuiItem(
                dropItem,
                event -> handleDropItemClick(player, dropItem, event)
            ))
        );
        gui.setDefaultClickAction(event -> handleInventoryClick(player, event));

        scheduler.runSync(() -> gui.open(player));
    }

    private void handleDropItemClick(Player player, DropItem dropItem, InventoryClickEvent event) {
        switch (event.getClick()) {
            case LEFT -> promptForChance(player).thenAccept(chance -> updateDropChance(dropItem, chance, player));
            case SHIFT_LEFT -> removeDropItem(dropItem, player);
        }
    }

    private void handleInventoryClick(Player player, InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (dropService.getDropItem(clickedItem).isPresent()) {
            langConfig.itemAlreadyAdded.sendTo(player);
            return;
        }
        if (event.getClickedInventory() instanceof PlayerInventory) {
            promptForChance(player).thenAccept(chance -> addNewDrop(clickedItem, chance, player));
        }
    }

    private CompletableFuture<Double> promptForChance(Player player) {
        CompletableFuture<Double> future = new CompletableFuture<>();
        try {
            String[] lines = langConfig.editDropSignLines.toArray(new String[0]);
            SignGUI.builder()
                .setLines(lines)
                .setHandler((signPlayer, result) -> {
                    String input = result.getLine(0);
                    try {
                        double chance = Double.parseDouble(input);
                        if (chance < 0 || chance > 100) throw new NumberFormatException();
                        future.complete(chance);
                        return List.of();
                    } catch (NumberFormatException e) {
                        return List.of(
                            SignGUIAction.displayNewLines(lines),
                            SignGUIAction.run(() -> langConfig.wrongChance.sendTo(signPlayer))
                        );
                    }
                })
                .build()
                .open(player);
        } catch (SignGUIVersionException e) {
            throw new RuntimeException(e);
        }
        return future;
    }

    private void updateDropChance(DropItem dropItem, double chance, Player player) {
        dropItem.chance = chance;
        saveConfigAndRefresh(player);
    }

    private void removeDropItem(DropItem dropItem, Player player) {
        dropConfig.dropItems.remove(dropItem);
        saveConfigAndRefresh(player);
    }

    private void addNewDrop(ItemStack item, double chance, Player player) {
        dropConfig.dropItems.add(new DropItem(item, chance));
        saveConfigAndRefresh(player);
    }

    private void saveConfigAndRefresh(Player player) {
        dropConfig.save();
        editDrops(player);
    }

    private GuiItem createDropGuiItem(@NonNull DropItem dropItem, @NonNull GuiAction<InventoryClickEvent> action) {
        return ItemStackBuilder.of(dropItem.itemStack)
            .appendLore(
                langConfig.dropGuiLoreAddition.with("{chance}", dropItem.chance).format()
            )
            .asGuiItem(action);
    }
}
