package me.drownek.skydrops.airdrop;

import eu.okaeri.commons.RandomNumbers;
import lombok.Getter;
import me.drownek.skydrops.drop.items.DropConfig;
import me.drownek.util.RandomUtil;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Objects;

@Getter
public class FallingPackageEntity extends PackageEntity {
    private final Plugin plugin;
    public final Location startLoc;
    public final Location target;
    public ArmorStand armorStand;
    private final AirdropService airdropService;
    private final DropConfig dropConfig;

    public FallingPackageEntity(final Plugin plugin, final Location loc, final Location target, AirdropService airdropService, DropConfig dropConfig) {
        super(plugin);
        this.plugin = plugin;
        this.target = target;
        this.airdropService = airdropService;
        this.dropConfig = dropConfig;
        this.armorStand = null;
        this.startLoc = loc;
        this.summon();
    }

    @Override
    public void summon() {
        Objects.requireNonNull(startLoc.getWorld(), "World is null");
        this.armorStand = (ArmorStand) startLoc.getWorld().spawnEntity(startLoc, EntityType.ARMOR_STAND);

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

        int blockY = this.armorStand.getLocation().getBlockY();
        int targetBlockY = target.getBlockY();

        if (blockY == targetBlockY) {
            airdropService.updateHologram();
            Bukkit.getScheduler().runTask(plugin, () -> {
                target.getBlock().setType(Material.CHEST);

                dropConfig.dropItems.stream()
                    .filter(dropItem -> RandomNumbers.chance(dropItem.chance))
                    .forEach(drop -> ((Chest) target.getBlock().getState()).getInventory().addItem(drop.itemStack));
            });
            remove();
            return;
        }

        ++this.counter;

        if (this.counter % 5 == 0 && ((this.armorStand.getLocation().getY() - target.getY()) > 3 || counter > 100)) {
            this.summonUpdateFireworks();
        }

        this.retick();
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