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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Getter
public class FallingPackageEntity extends PackageEntity {
    private final Plugin plugin;
    public final Location startLoc;
    public ArmorStand armorStand;
    private final AirdropService airdropService;
    private final DropConfig dropConfig;
    private @Setter int hp;
    private final Block targetBlock;
    private final AirdropConfig airdropConfig;

    public FallingPackageEntity(final Plugin plugin, final Location loc, AirdropService airdropService, DropConfig dropConfig, int hp, AirdropConfig airdropConfig) {
        super(plugin);
        this.plugin = plugin;
        this.airdropService = airdropService;
        this.dropConfig = dropConfig;
        this.armorStand = null;
        this.startLoc = LocationUtil.toCenter(loc);
        this.hp = hp;
        this.targetBlock = calculateTargetBlock();
        this.airdropConfig = airdropConfig;
        this.summon();
    }

    @Override
    public void summon() {
        World world = Objects.requireNonNull(startLoc.getWorld());
        this.armorStand = (ArmorStand) world.spawnEntity(startLoc, EntityType.ARMOR_STAND);

        armorStand.setVisible(false);
        //noinspection deprecation
        armorStand.setHelmet(new ItemStack(Material.CHEST, 1));
        armorStand.setBasePlate(false);
        armorStand.setGravity(true);

        this.summonSpawnFireworks();
        this.tick();
    }

    public void tick() {
        if (armorStand.isDead()) return;

        Vector v = armorStand.getVelocity();
        double speed = 0.1;
        v.setY(-(speed));
        armorStand.setVelocity(v);

        int armorStandBlockY = this.armorStand.getLocation().getBlockY();
        Block targetBlock = getTargetBlock();
        int targetBlockY = targetBlock.getY();

        if (armorStandBlockY == targetBlockY) {
            airdropService.updateHologram(this);
            Bukkit.getScheduler().runTask(plugin, () -> {
                targetBlock.setType(Material.CHEST);

                Chest chest = (Chest) targetBlock.getState();

                dropConfig.dropItems.stream()
                    .filter(dropItem -> RandomNumbers.chance(dropItem.chance))
                    .forEach(drop -> {
                        if (airdropConfig.randomizeItemSlots) {
                            int size = chest.getInventory().getSize();
                            int slot = RandomUtil.randomInteger(0, size);

                            for (int i = 0; i < size; i++) {
                                int s = (slot + i) % size;
                                if (chest.getInventory().getItem(s) == null) {
                                    chest.getInventory().setItem(s, drop.itemStack);
                                    return;
                                }
                            }

                            chest.getInventory().addItem(drop.itemStack);
                        } else {
                            chest.getInventory().addItem(drop.itemStack);
                        }
                    });

            });
            remove();
            return;
        }

        ++this.counter;

        if (this.counter % 5 == 0 && ((this.armorStand.getLocation().getY() - targetBlockY) > 3 || counter > 100)) {
            this.summonUpdateFireworks();
        }

        this.retick();
    }

    public Location getTarget() {
        return LocationUtil.toCenter(getTargetBlock().getLocation());
    }

    private Block calculateTargetBlock() {
        World world = Objects.requireNonNull(startLoc.getWorld());

        int x = startLoc.getBlockX();
        int z = startLoc.getBlockZ();

        for (int y = startLoc.getBlockY(); y >= world.getMinHeight(); y--) {
            Block block = world.getBlockAt(x, y, z);
            if (!block.isEmpty()) {
                return world.getBlockAt(x, y + 1, z);
            }
        }
        return null;
    }

    @Override
    public void remove() {
        this.armorStand.remove();
    }

    private void summonUpdateFireworks() {
        Location loc = this.armorStand.getLocation().clone().add(0, 3, 0);
        final Firework fw = (Firework) Objects.requireNonNull(startLoc.getWorld()).spawnEntity(loc, EntityType.FIREWORK);
        final FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(Color.RED).withColor(Color.WHITE).build());
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
        plugin.getServer().getScheduler().runTaskLater(plugin, fw::detonate, 1L);
    }

    private void summonSpawnFireworks() {
        Location loc = this.armorStand.getLocation().clone().add(0, 3, 0);
        final Firework fw = (Firework) Objects.requireNonNull(startLoc.getWorld()).spawnEntity(loc, EntityType.FIREWORK);
        final FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.WHITE).build());
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);

        plugin.getServer().getScheduler().runTaskLater(plugin, fw::detonate, 1L);
    }
}