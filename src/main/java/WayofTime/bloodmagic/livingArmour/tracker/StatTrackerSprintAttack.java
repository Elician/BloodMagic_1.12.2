package WayofTime.bloodmagic.livingArmour.tracker;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.livingArmour.LivingArmourUpgrade;
import WayofTime.bloodmagic.livingArmour.StatTracker;
import WayofTime.bloodmagic.livingArmour.LivingArmour;
import WayofTime.bloodmagic.livingArmour.upgrade.LivingArmourUpgradeSprintAttack;
import WayofTime.bloodmagic.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StatTrackerSprintAttack extends StatTracker {
    public static HashMap<LivingArmour, Double> changeMap = new HashMap<>();
    public static int[] damageRequired = new int[]{200, 800, 1300, 2500, 3800};
    public double totalDamageDealt = 0;

    @Override
    public String getUniqueIdentifier() {
        return BloodMagic.MODID + ".tracker.sprintAttack";
    }

    @Override
    public void resetTracker() {
        this.totalDamageDealt = 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        totalDamageDealt = tag.getDouble(BloodMagic.MODID + ".tracker.sprintAttack");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setDouble(BloodMagic.MODID + ".tracker.sprintAttack", totalDamageDealt);
    }

    @Override
    public boolean onTick(World world, EntityPlayer player, LivingArmour livingArmour) {
        if (changeMap.containsKey(livingArmour)) {
            double change = Math.abs(changeMap.get(livingArmour));
            if (change > 0) {
                totalDamageDealt += Math.abs(changeMap.get(livingArmour));

                changeMap.put(livingArmour, 0d);

                this.markDirty();

                return true;
            }
        }

        return false;
    }

    @Override
    public void onDeactivatedTick(World world, EntityPlayer player, LivingArmour livingArmour) {
        if (changeMap.containsKey(livingArmour)) {
            changeMap.remove(livingArmour);
        }
    }

    @Override
    public List<LivingArmourUpgrade> getUpgrades() {
        List<LivingArmourUpgrade> upgradeList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            if (totalDamageDealt >= damageRequired[i]) {
                upgradeList.add(new LivingArmourUpgradeSprintAttack(i));
            }
        }

        return upgradeList;
    }

    @Override
    public double getProgress(LivingArmour livingArmour, int currentLevel) {
        return Utils.calculateStandardProgress(totalDamageDealt, damageRequired, currentLevel);
    }

    @Override
    public boolean providesUpgrade(String key) {
        return key.equals(BloodMagic.MODID + ".upgrade.sprintAttack");
    }

    @Override
    public void onArmourUpgradeAdded(LivingArmourUpgrade upgrade) {
        if (upgrade instanceof LivingArmourUpgradeSprintAttack) {
            int level = upgrade.getUpgradeLevel();
            if (level < damageRequired.length) {
                totalDamageDealt = Math.max(totalDamageDealt, damageRequired[level]);
                this.markDirty();
            }
        }
    }

    public static void incrementCounter(LivingArmour armour, double damage) {
        changeMap.put(armour, changeMap.containsKey(armour) ? changeMap.get(armour) + damage : damage);
    }
}
