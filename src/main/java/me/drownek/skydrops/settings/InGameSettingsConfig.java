package me.drownek.skydrops.settings;

import eu.okaeri.configs.OkaeriConfig;
import lombok.NonNull;
import me.drownek.platform.core.annotation.Configuration;
import me.drownek.util.TimeUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@Configuration(path = "ingame-settings.{ext}")
public class InGameSettingsConfig extends OkaeriConfig {

    private Map<String, Object> settingsValues = new HashMap<>(); {
        initializeDefaults();
    }

    public void initializeDefaults() {
        for (InGameSettingsType type : InGameSettingsType.values()) {
            if (!settingsValues.containsKey(type.name())) {
                settingsValues.put(type.name(), type.getDefaultValue());
            }
        }
    }

    public <T> T getValue(InGameSettingsType settingType, Class<T> expectedType) {
        Object value = settingsValues.get(settingType.name());

        if (value == null) {
            return settingType.getDefaultValue(expectedType);
        }

        if (value instanceof String string && expectedType.equals(Duration.class)) {
            Duration parsedDuration = Duration.ofMillis(TimeUtil.timeFromString(string));
            if (parsedDuration != null) {
                settingsValues.put(settingType.name(), parsedDuration);
                return expectedType.cast(parsedDuration);
            }
        }

        if (!value.getClass().equals(expectedType)) {
            throw new IllegalArgumentException("Type mismatch for setting " + settingType.name() +
                ": expected " + expectedType + ", setting type is " + value.getClass());
        }

        return expectedType.cast(value);
    }

    public <T> void setValue(InGameSettingsType settingType, @NonNull T value) {
        if (!settingType.getDefaultValue().getClass().equals(value.getClass())) {
            throw new IllegalArgumentException("Invalid value type for setting " + settingType.name() + 
                ": expected " + settingType.getDefaultValue().getClass() + ", got " + value.getClass());
        }

        settingsValues.put(settingType.name(), value);
    }

    public void resetAllToDefaults() {
        settingsValues.clear();
        initializeDefaults();
    }
}