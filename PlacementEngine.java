package net.kronos.automation.builder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.kronos.KronosLogger;

import java.util.*;

public class PlacementEngine {
    
    private List<BlockPos> blocksToPlace;
    private Set<BlockPos> placedBlocks;
    private int currentIndex;
    private long lastPlacementTime;
    private int placementDelay;
    private boolean isRunning;
    
    public PlacementEngine() {
        this.blocksToPlace = new ArrayList<>();
        this.placedBlocks = new HashSet<>();
        this.currentIndex = 0;
        this.lastPlacementTime = 0;
        this.placementDelay = 50; // milliseconds
        this.isRunning = false;
    }
    
    public void start(List<BlockPos> positions) {
        this.blocksToPlace = new ArrayList<>(positions);
        this.placedBlocks.clear();
        this.currentIndex = 0;
        this.isRunning = true;
        KronosLogger.info("Starting placement of " + blocksToPlace.size() + " blocks");
    }
    
    public void stop() {
        this.isRunning = false;
        this.blocksToPlace.clear();
        this.placedBlocks.clear();
        KronosLogger.info("Placement stopped");
    }
    
    public void tick() {
        if (!isRunning || blocksToPlace.isEmpty()) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        
        // Check if we should place next block
        if (System.currentTimeMillis() - lastPlacementTime < placementDelay) {
            return;
        }
        
        // Find next block to place
        while (currentIndex < blocksToPlace.size()) {
            BlockPos pos = blocksToPlace.get(currentIndex);
            currentIndex++;
            
            if (isBlockPlaced(client.world, pos)) {
                placedBlocks.add(pos);
                continue;
            }
            
            // Try to place block
            if (placeBlock(client, pos)) {
                placedBlocks.add(pos);
                lastPlacementTime = System.currentTimeMillis();
                return;
            }
        }
        
        isRunning = false;
    }
    
    private boolean placeBlock(MinecraftClient client, BlockPos targetPos) {
        PlayerInventory inventory = client.player.getInventory();
        
        // Find block in inventory
        ItemStack blockStack = null;
        int slotIndex = -1;
        
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (stack.getItem() instanceof BlockItem && !stack.isEmpty()) {
                blockStack = stack;
                slotIndex = i;
                break;
            }
        }
        
        if (blockStack == null || slotIndex == -1) {
            return false;
        }
        
        // Switch to block
        inventory.selectedSlot = slotIndex;
        
        // Find valid placement position
        BlockHitResult hitResult = findValidPlacement(client.world, targetPos);
        if (hitResult == null) {
            return false;
        }
        
        // Send placement packet
        client.getNetworkHandler().sendPacket(
            new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult)
        );
        
        return true;
    }
    
    private BlockHitResult findValidPlacement(World world, BlockPos targetPos) {
        // Try all 6 faces of the target position
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = targetPos.offset(direction);
            
            // Check if neighbor block exists and is solid
            if (world.getBlockState(neighborPos).getMaterial().isSolid()) {
                Vec3d hitPos = new Vec3d(
                    targetPos.getX() + 0.5,
                    targetPos.getY() + 0.5,
                    targetPos.getZ() + 0.5
                );
                
                return new BlockHitResult(hitPos, direction.getOpposite(), neighborPos, false);
            }
        }
        
        return null;
    }
    
    private boolean isBlockPlaced(World world, BlockPos pos) {
        return !world.getBlockState(pos).isAir();
    }
    
    public void setPlacementDelay(int delayMs) {
        this.placementDelay = delayMs;
    }
    
    public boolean isComplete() {
        return currentIndex >= blocksToPlace.size();
    }
    
    public int getProgress() {
        return placedBlocks.size();
    }
    
    public int getTotalBlocks() {
        return blocksToPlace.size();
    }
    
    public float getProgressPercent() {
        if (blocksToPlace.isEmpty()) {
            return 0f;
        }
        return (float) placedBlocks.size() / blocksToPlace.size() * 100f;
    }
}
