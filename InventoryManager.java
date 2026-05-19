package net.kronos.automation.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import java.util.*;

public class InventoryManager {
    
    private Set<Item> whitelist;
    private Set<Item> blacklist;
    private boolean autoSort;
    private boolean autoJunk;
    
    public InventoryManager() {
        this.whitelist = new HashSet<>();
        this.blacklist = new HashSet<>();
        this.autoSort = false;
        this.autoJunk = false;
        
        initializeDefaults();
    }
    
    private void initializeDefaults() {
        // Add important items to whitelist
        whitelist.add(Items.DIAMOND);
        whitelist.add(Items.EMERALD);
        whitelist.add(Items.NETHERITE_INGOT);
        whitelist.add(Items.NETHERITE_PICKAXE);
        whitelist.add(Items.DIAMOND_PICKAXE);
        whitelist.add(Items.IRON_PICKAXE);
        whitelist.add(Items.TOTEM_OF_UNDYING);
        whitelist.add(Items.GOLDEN_APPLE);
        
        // Add junk items to blacklist
        blacklist.add(Items.DIRT);
        blacklist.add(Items.GRAVEL);
        blacklist.add(Items.SAND);
    }
    
    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        
        if (autoSort) {
            sortInventory(client.player);
        }
        
        if (autoJunk) {
            dropJunk(client.player);
        }
    }
    
    public void sortInventory(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        
        // Sort main inventory
        List<ItemStack> stacks = new ArrayList<>(inventory.main);
        Collections.sort(stacks, (a, b) -> {
            // Prioritize whitelisted items
            boolean aWhitelisted = whitelist.contains(a.getItem());
            boolean bWhitelisted = whitelist.contains(b.getItem());
            if (aWhitelisted != bWhitelisted) {
                return aWhitelisted ? -1 : 1;
            }
            
            // Sort by item ID
            return Integer.compare(Item.getRawId(a.getItem()), Item.getRawId(b.getItem()));
        });
        
        for (int i = 0; i < stacks.size(); i++) {
            inventory.main.set(i, stacks.get(i));
        }
    }
    
    public void dropJunk(PlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            
            if (stack.isEmpty()) {
                continue;
            }
            
            if (blacklist.contains(stack.getItem())) {
                inventory.removeStack(i);
            }
        }
    }
    
    public ItemStack findItem(Item item) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return ItemStack.EMPTY;
        }
        
        PlayerInventory inventory = client.player.getInventory();
        
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (stack.getItem() == item) {
                return stack;
            }
        }
        
        return ItemStack.EMPTY;
    }
    
    public int findItemSlot(Item item) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return -1;
        }
        
        PlayerInventory inventory = client.player.getInventory();
        
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        
        return -1;
    }
    
    public int getEmptySlots() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return 0;
        }
        
        int empty = 0;
        for (ItemStack stack : client.player.getInventory().main) {
            if (stack.isEmpty()) {
                empty++;
            }
        }
        
        return empty;
    }
    
    public boolean isFull() {
        return getEmptySlots() == 0;
    }
    
    public void addToWhitelist(Item item) {
        whitelist.add(item);
    }
    
    public void removeFromWhitelist(Item item) {
        whitelist.remove(item);
    }
    
    public void addToBlacklist(Item item) {
        blacklist.add(item);
    }
    
    public void removeFromBlacklist(Item item) {
        blacklist.remove(item);
    }
    
    public void setAutoSort(boolean autoSort) {
        this.autoSort = autoSort;
    }
    
    public void setAutoJunk(boolean autoJunk) {
        this.autoJunk = autoJunk;
    }
}
