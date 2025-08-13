package me.drownek.skydrops.drop.items;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class DropItem extends OkaeriConfig {

    public ItemStack itemStack;
    public double chance;
}
