package net.kronos.automation.survival;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.kronos.KronosLogger;

public class AutoTotem {
    
    private float healthThreshold;
    private boolean legit;
    private long lastTotemTime;
    private int totemDelay;
    private boolean enabled;
    
    private int totems;
    private boolean isHoldingTotem;
    
    public AutoTotem() {
        this.healthThreshold = 7.0f;
        this.legit = true;
        this.lastTotemTime = 0;
        this.totemDelay = 100;
        this.enabled = true;
        this.totems = 0;
        this.isHoldingTotem = false;
    }
    
    public void tick() {
        if (!enabled) {
            return;
        }
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return;
        }
        
        updateTotemCount(client.player);
        checkAndEquipTotem(client);
    }
    
    private void checkAndEquipTotem(MinecraftClient client) {
        PlayerEntity player = client.player;
        
        // Check health
        float health = player.getHealth();
        if (health > healthThreshold) {
            isHoldingTotem = false;
            return;
        }
        
        // Check if we have totems
        if (totems <= 0) {
            return;
        }
        
        // Check cooldown
        if (System.currentTimeMillis() - lastTotemTime < totemDelay) {
            return;
        }
        
        // Equip totem
        if (equipTotem(client)) {
            lastTotemTime = System.currentTimeMillis();
            isHoldingTotem = true;
        }
    }
    
    private boolean equipTotem(MinecraftClient client) {
        PlayerEntity player = client.player;
        PlayerInventory inventory = player.getInventory();
        
        // Find totem in inventory
        int totemSlot = -1;
        for (int i = 0; i < inventory.main.size(); i++) {
            ItemStack stack = inventory.main.get(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                totemSlot = i;
                break;
            }
        }
        
        if (totemSlot == -1) {
            return false;
        }
        
        // Click on offhand slot
        if (legit) {
            // Legit mode: use click packet
            client.getNetworkHandler().sendPacket(
                new ClickSlotC2SPacket(
                    0, // Container window ID
                    1, // State ID
                    45, // Offhand slot
                    SlotActionType.SWAP,
                    new ItemStack(Items.TOTEM_OF_UNDYING)
                )
            );
        } else {
            // Direct mode: set offhand directly
            inventory.offHand.set(0, new ItemStack(Items.TOTEM_OF_UNDYING));
        }
        
        return true;
    }
    
    private void updateTotemCount(PlayerEntity player) {
        totems = 0;
        
        // Count totems in inventory
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            ItemStack stack = player.getInventory().main.get(i);
            if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                totems += stack.getCount();
            }
        }
        
        // Check offhand
        if (player.getInventory().offHand.get(0).getItem() == Items.TOTEM_OF_UNDYING) {
            totems += player.getInventory().offHand.get(0).getCount();
        }
    }
    
    public void setHealthThreshold(float threshold) {
        this.healthThreshold = threshold;
    }
    
    public void setLegitMode(boolean legit) {
        this.legit = legit;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getTotemCount() {
        return totems;
    }
    
    public float getHealthThreshold() {
        return healthThreshold;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}
