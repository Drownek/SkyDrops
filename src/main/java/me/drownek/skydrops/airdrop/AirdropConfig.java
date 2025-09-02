package me.drownek.skydrops.airdrop;

import eu.okaeri.configs.OkaeriConfig;
import me.drownek.platform.core.annotation.Configuration;

@Configuration(path = "airdrops.{ext}")
public class AirdropConfig extends OkaeriConfig {

    public String airdropsWorld = "world";
    public boolean randomizeItemSlots = true;
}
