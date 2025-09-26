package me.drownek.skydrops.drop.items;

import eu.okaeri.injector.annotation.Inject;
import lombok.NonNull;
import me.drownek.platform.core.annotation.Component;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Component
public class DropService {

    private @Inject DropConfig dropConfig;

    public Optional<DropItem> getDropItem(@NonNull ItemStack itemStack) {
        return dropConfig.dropItems.stream()
            .filter(dropItem -> dropItem.itemStack.equals(itemStack))
            .findFirst();
    }
}