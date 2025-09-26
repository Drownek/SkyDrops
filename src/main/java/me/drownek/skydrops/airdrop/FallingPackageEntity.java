package me.drownek.skydrops.airdrop;

import eu.okaeri.commons.RandomNumbers;
import lombok.Getter;
import lombok.Setter;
import me.drownek.skydrops.drop.items.DropConfig;
import me.drownek.util.LocationUtil;
import me.drownek.util.RandomUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
public class FallingPackageEntity extends PackageEntity {
    
    private static final double FALL_SPEED = 0.1;
    private static final int FIREWORK_INTERVAL = 5;
    private static final int MINIMUM_HEIGHT_DIFFERENCE = 3;
    private static final int MAX_FALL_TICKS = 100;
    
    private final Plugin plugin;
    private final Location startLocation;
    private final AirdropService airdropService;
    private final DropConfig dropConfig;
    private final Block targetBlock;
    private final AirdropConfig airdropConfig;
    private @Nullable Instant autoDespawnTime;

    private int hp;
    public ArmorStand armorStand;

    public FallingPackageEntity(Plugin plugin, Location location, AirdropService airdropService, 
                               DropConfig dropConfig, int hp, AirdropConfig airdropConfig) {
        super(plugin);
        this.plugin = plugin;
        this.airdropService = airdropService;
        this.dropConfig = dropConfig;
        this.startLocation = LocationUtil.toCenter(location);
        this.hp = hp;
        this.targetBlock = calculateTargetBlock();
        this.airdropConfig = airdropConfig;
    }

    @Override
    public void summon() {
        World world = Objects.requireNonNull(startLocation.getWorld());
        this.armorStand = createArmorStand(world);
        
        spawnInitialFireworks();
        tick();
    }

    @Override
    protected void tick() {
        if (armorStand.isDead()) {
            return;
        }

        applyFallVelocity();

        if (hasReachedTarget()) {
            handleLanding();
            return;
        }

        counter++;
        
        if (shouldSpawnFireworks()) {
            spawnTrailFireworks();
        }

        retick();
    }

    public Location getTarget() {
        return LocationUtil.toCenter(targetBlock.getLocation());
    }

    private ArmorStand createArmorStand(World world) {
        ArmorStand entity = (ArmorStand) world.spawnEntity(startLocation, EntityType.ARMOR_STAND);
        
        entity.setVisible(false);
        entity.setHelmet(new ItemStack(Material.CHEST));
        entity.setBasePlate(false);
        entity.setGravity(true);
        
        return entity;
    }

    private void applyFallVelocity() {
        Vector velocity = armorStand.getVelocity();
        velocity.setY(-FALL_SPEED);
        armorStand.setVelocity(velocity);
    }

    private boolean hasReachedTarget() {
        int currentY = armorStand.getLocation().getBlockY();
        int targetY = targetBlock.getY();
        return currentY == targetY;
    }

    private void handleLanding() {
        airdropService.updateHologram(this);
        
        Bukkit.getScheduler().runTask(plugin, () -> {
            createChestAtTarget();
            populateChestWithDrops();
        });

        armorStand.remove();
    }

    private boolean shouldSpawnFireworks() {
        int currentY = armorStand.getLocation().getBlockY();
        int targetY = targetBlock.getY();
        int heightDifference = currentY - targetY;
        
        return counter % FIREWORK_INTERVAL == 0 && 
               (heightDifference > MINIMUM_HEIGHT_DIFFERENCE || counter > MAX_FALL_TICKS);
    }

    private Block calculateTargetBlock() {
        World world = Objects.requireNonNull(startLocation.getWorld());
        
        int x = startLocation.getBlockX();
        int z = startLocation.getBlockZ();

        for (int y = startLocation.getBlockY(); y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (!block.isEmpty()) {
                return world.getBlockAt(x, y + 1, z);
            }
        }
        
        return world.getBlockAt(x, world.getMinHeight(), z);
    }

    private void createChestAtTarget() {
        targetBlock.setType(Material.CHEST);
    }

    private void populateChestWithDrops() {
        Chest chest = (Chest) targetBlock.getState();
        
        dropConfig.dropItems.stream()
            .filter(dropItem -> RandomNumbers.chance(dropItem.chance))
            .forEach(dropItem -> addItemToChest(chest, dropItem.itemStack));
    }

    private void addItemToChest(Chest chest, ItemStack item) {
        if (airdropConfig.randomizeItemSlots) {
            addItemToRandomSlot(chest, item);
        } else {
            chest.getInventory().addItem(item);
        }
    }

    private void addItemToRandomSlot(Chest chest, ItemStack item) {
        int inventorySize = chest.getInventory().getSize();
        int startSlot = RandomUtil.randomInteger(0, inventorySize);

        for (int i = 0; i < inventorySize; i++) {
            int slot = (startSlot + i) % inventorySize;
            if (chest.getInventory().getItem(slot) == null) {
                chest.getInventory().setItem(slot, item);
                return;
            }
        }

        chest.getInventory().addItem(item);
    }

    private void spawnTrailFireworks() {
        Location fireworkLocation = armorStand.getLocation().clone().add(0, 3, 0);
        Firework firework = createFirework(fireworkLocation, FireworkEffect.Type.BALL, Color.RED, Color.WHITE);
        scheduleFireworkDetonation(firework);
    }

    private void spawnInitialFireworks() {
        Location fireworkLocation = armorStand.getLocation().clone().add(0, 3, 0);
        Firework firework = createFirework(fireworkLocation, FireworkEffect.Type.BALL_LARGE, Color.RED, Color.WHITE);
        scheduleFireworkDetonation(firework);
    }

    private Firework createFirework(Location location, FireworkEffect.Type type, Color... colors) {
        World world = Objects.requireNonNull(startLocation.getWorld());
        Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
        
        FireworkMeta meta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
            .with(type)
            .withColor(colors)
            .build();
            
        meta.addEffect(effect);
        meta.setPower(1);
        firework.setFireworkMeta(meta);
        
        return firework;
    }

    private void scheduleFireworkDetonation(Firework firework) {
        plugin.getServer().getScheduler().runTaskLater(plugin, firework::detonate, 1L);
    }
}