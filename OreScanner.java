package net.kronos.automation.miner;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import java.util.*;

public class OreScanner {
    
    private static final Set<Integer> ORE_BLOCKS = new HashSet<>();
    
    static {
        // Add valuable ore blocks
        ORE_BLOCKS.add(Blocks.DIAMOND_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.GOLD_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.EMERALD_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.ANCIENT_DEBRIS.hashCode());
        ORE_BLOCKS.add(Blocks.IRON_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.LAPIS_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.COPPER_ORE.hashCode());
        ORE_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE.hashCode());
    }
    
    private Map<BlockPos, Long> discoveredOres;
    private boolean scanDiamondsOnly;
    
    public OreScanner() {
        this.discoveredOres = new HashMap<>();
        this.scanDiamondsOnly = false;
    }
    
    public void scanChunks(int radiusChunks) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) {
            return;
        }
        
        BlockPos playerPos = client.player.getBlockPos();
        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;
        
        // Scan loaded chunks around player
        for (int cx = playerChunkX - radiusChunks; cx <= playerChunkX + radiusChunks; cx++) {
            for (int cz = playerChunkZ - radiusChunks; cz <= playerChunkZ + radiusChunks; cz++) {
                if (client.world.isChunkLoaded(cx, cz)) {
                    scanChunk(client.world.getChunk(cx, cz));
                }
            }
        }
    }
    
    private void scanChunk(WorldChunk chunk) {
        if (chunk == null) {
            return;
        }
        
        // Scan all blocks in chunk
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = chunk.getBottomBlockY(); y < chunk.getTopBlockY(); y++) {
                    BlockPos pos = chunk.getPos().getBlockPos().add(x, y, z);
                    
                    if (isOreBlock(chunk.getBlockState(x, y, z).getBlock())) {
                        if (!discoveredOres.containsKey(pos)) {
                            discoveredOres.put(pos, System.currentTimeMillis());
                        }
                    }
                }
            }
        }
    }
    
    public BlockPos getNearestOre(BlockPos fromPos, int maxDistance) {
        if (discoveredOres.isEmpty()) {
            return null;
        }
        
        BlockPos nearest = null;
        double nearestDist = maxDistance * maxDistance;
        
        for (BlockPos orePos : discoveredOres.keySet()) {
            double dist = fromPos.getSquaredDistance(orePos);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = orePos;
            }
        }
        
        return nearest;
    }
    
    public List<BlockPos> getNearbyOres(BlockPos center, int radius, int maxOres) {
        List<BlockPos> nearby = new ArrayList<>();
        int radiusSq = radius * radius;
        
        for (BlockPos pos : discoveredOres.keySet()) {
            if (center.getSquaredDistance(pos) <= radiusSq) {
                nearby.add(pos);
                if (nearby.size() >= maxOres) {
                    break;
                }
            }
        }
        
        // Sort by distance
        nearby.sort((a, b) -> Double.compare(
            center.getSquaredDistance(a),
            center.getSquaredDistance(b)
        ));
        
        return nearby;
    }
    
    public boolean isOreBlock(net.minecraft.block.Block block) {
        if (scanDiamondsOnly) {
            return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE;
        }
        return ORE_BLOCKS.contains(block.hashCode());
    }
    
    public void clearOldOres(long ageMs) {
        long currentTime = System.currentTimeMillis();
        discoveredOres.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > ageMs
        );
    }
    
    public int getOreCount() {
        return discoveredOres.size();
    }
    
    public void setScanDiamondsOnly(boolean diamondsOnly) {
        this.scanDiamondsOnly = diamondsOnly;
    }
}
