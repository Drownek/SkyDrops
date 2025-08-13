package me.drownek.skydrops.lang;

import eu.okaeri.configs.OkaeriConfig;
import me.drownek.platform.core.annotation.Configuration;
import me.drownek.util.message.SendableMessage;

import java.util.List;

@Configuration(path = "lang.{ext}")
public class LangConfig extends OkaeriConfig {

    public SendableMessage wrongChance = SendableMessage.of("&cWrong chance!");
    public SendableMessage itemAlreadyAdded = SendableMessage.of("&cItem is already added to the drop!");
    public String editDropGuiTitle = "Click item in inventory to add";
    public List<String> editDropSignLines = List.of("", "/\\/\\/\\/\\/\\/\\/\\/\\", "Enter chance");

}
