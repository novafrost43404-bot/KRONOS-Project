package net.kronos.automation.miner;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.kronos.KronosLogger;

import java.util.*;

public class MiningEngine {
    
    private enum MiningState {
        SEARCHING, PATHFINDING, MINING, RETURNING
    }
    
    private MiningState state;
    private BlockPos targetOre;
    private OreScanner oreScanner;
    private long lastMineTime;
    private int mineDelay;
    private boolean isRunning;
    private long mineStartTime;
    
    private int oresMined;
    private boolean diamondsOnly;
    
    public MiningEngine() {
        this.oreScanner = new OreScanner();
        this.state = MiningState.SEARCHING;
        this.lastMineTime = 0;
        this.mineDelay = 500; // milliseconds
        this.isRunning = false;
        this.oresMined = 0;
        this.diamondsOnly = false;
    }
    
    public void start() {
        this.isRunning = true;
        this.state = MiningState.SEARCHING;
        this.oresMined = 0;
        KronosLogger.info("Mining engine started");
    }
    
    public void stop() {
        this.isRunning = false;
        this.state = MiningState.SEARCHING;
        this.targetOre = null;
        KronosLogger.info("Mining stopped. Total ores mined: " + oresMined);
    }
    
    public void tick() {
        if (!isRunning) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        
        switch (state) {
            case SEARCHING:
                searchForOres(client);
                break;
            case MINING:
                mineCurrentOre(client);
                break;
            case PATHFINDING:
                pathfindToOre(client);
                break;
            case RETURNING:
                returnToSpawn(client);
                break;
        }
    }
    
    private void searchForOres(MinecraftClient client) {
        // Scan nearby chunks for ores
        oreScanner.scanChunks(5);
        
        // Find nearest ore
        BlockPos playerPos = client.player.getBlockPos();
        targetOre = oreScanner.getNearestOre(playerPos, 128);
        
        if (targetOre != null) {
            state = MiningState.PATHFINDING;
            mineStartTime = System.currentTimeMillis();
        }
    }
    
    private void pathfindToOre(MinecraftClient client) {
        if (targetOre == null) {
            state = MiningState.SEARCHING;
            return;
        }
        
        PlayerEntity player = client.player;
        BlockPos playerPos = player.getBlockPos();
        
        // Simple pathfinding: move towards ore
        double distToOre = Math.sqrt(playerPos.getSquaredDistance(targetOre));
        
        if (distToOre < 2.0) {
            state = MiningState.MINING;
            return;
        }
        
        // Move towards ore
        Vec3d oreVec = Vec3d.of(targetOre);
        Vec3d playerVec = player.getPos();
        Vec3d direction = oreVec.subtract(playerVec).normalize();
        
        player.setVelocity(direction.multiply(0.15));
        player.velocityDirty = true;
    }
    
    private void mineCurrentOre(MinecraftClient client) {
        if (targetOre == null || !isOreAt(client.world, targetOre)) {
            oresMined++;
            state = MiningState.SEARCHING;
            return;
        }
        
        // Check if enough time has passed
        if (System.currentTimeMillis() - lastMineTime < mineDelay) {
            return;
        }
        
        // Select pickaxe
        selectPickaxe(client.player);
        
        // Look at ore
        lookAtBlock(client.player, targetOre);
        
        // Start mining
        client.interactionManager.attackBlock(targetOre, Direction.UP);
        lastMineTime = System.currentTimeMillis();
        
        // Check if ore is broken
        if (!isOreAt(client.world, targetOre)) {
            oresMined++;
            state = MiningState.SEARCHING;
        }
    }
    
    private void returnToSpawn(MinecraftClient client) {
        // Return logic would be implemented here
        state = MiningState.SEARCHING;
    }
    
    private boolean isOreAt(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return oreScanner.isOreBlock(block);
    }
    
    private void selectPickaxe(PlayerEntity player) {
        int bestSlot = -1;
        int bestLevel = 0;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.getItem() instanceof PickaxeItem) {
                int level = getPickaxeLevel((PickaxeItem) stack.getItem());
                if (level > bestLevel) {
                    bestLevel = level;
                    bestSlot = i;
                }
            }
        }
        
        if (bestSlot != -1) {
            player.getInventory().selectedSlot = bestSlot;
        }
    }
    
    private int getPickaxeLevel(PickaxeItem pickaxe) {
        if (pickaxe == Items.NETHERITE_PICKAXE) return 4;
        if (pickaxe == Items.DIAMOND_PICKAXE) return 3;
        if (pickaxe == Items.IRON_PICKAXE) return 2;
        if (pickaxe == Items.STONE_PICKAXE) return 1;
        return 0;
    }
    
    private void lookAtBlock(PlayerEntity player, BlockPos target) {
        Vec3d targetVec = Vec3d.of(target).add(0.5, 0.5, 0.5);
        Vec3d playerVec = player.getEyePos();
        Vec3d direction = targetVec.subtract(playerVec);
        
        double yaw = Math.atan2(direction.z, direction.x) * 180 / Math.PI - 90;
        double pitch = Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * 180 / Math.PI;
        
        player.setYaw((float) yaw);
        player.setPitch((float) -pitch);
    }
    
    public void setMineDelay(int delayMs) {
        this.mineDelay = delayMs;
    }
    
    public void setDiamondsOnly(boolean diamondsOnly) {
        this.diamondsOnly = diamondsOnly;
        this.oreScanner.setScanDiamondsOnly(diamondsOnly);
    }
    
    public int getOresMined() {
        return oresMined;
    }
    
    public BlockPos getTargetOre() {
        return targetOre;
    }
    
    public int getNearbyOreCount() {
        return oreScanner.getOreCount();
    }
}
