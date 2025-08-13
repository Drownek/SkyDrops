package me.drownek.skydrops.drop.items;

import eu.okaeri.configs.OkaeriConfig;
import me.drownek.platform.core.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration(path = "drops.{ext}")
public class DropConfig extends OkaeriConfig {

    public List<DropItem> dropItems = new ArrayList<>();
}
