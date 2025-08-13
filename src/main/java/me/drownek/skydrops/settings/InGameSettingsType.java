package me.drownek.skydrops.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.drownek.util.TimeUtil;
import org.bukkit.Material;

import java.time.Duration;

@Getter
@AllArgsConstructor
public enum InGameSettingsType {
    AIRDROPS("AirDrops", Material.CHEST, false),
    AIRDROP_DELAY("Time between airdrops", Material.CLOCK, Duration.ofMinutes(5)),
    AIRDROP_HP("AirDrop HP", Material.APPLE, 20),
    AIRDROP_ANNOUNCEMENT_TIME("Time before drop to announce", Material.PAPER, Duration.ofSeconds(10));

    private final String name;
    private final Material icon;
    private final Object defaultValue;

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValue(Class<T> type) {
        if (!defaultValue.getClass().equals(type)) {
            throw new IllegalArgumentException("Type mismatch: expected " + defaultValue.getClass() + ", got " + type);
        }
        return (T) defaultValue;
    }

    public String formatValue(Object value) {
        if (value instanceof Duration duration) {
            return TimeUtil.formatDuration(duration);
        }
        return value.toString();
    }
}