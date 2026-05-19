package net.kronos.client.input;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    
    private KeyBinding keyStart;
    private KeyBinding keyPause;
    private KeyBinding keyResume;
    private KeyBinding keyEmergencyStop;
    private KeyBinding keyGuiOpen;
    private KeyBinding keyHudToggle;
    private KeyBinding keyMiningToggle;
    private KeyBinding keyBuilderToggle;
    private KeyBinding keyLegitToggle;
    
    private boolean lastStartState = false;
    private boolean lastPauseState = false;
    private boolean lastResumeState = false;
    private boolean lastEmergencyStopState = false;
    private boolean lastGuiState = false;
    private boolean lastHudState = false;
    
    public void registerKeybinds() {
        // Create keybindings with descriptive names
        this.keyStart = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.start",
            GLFW.GLFW_KEY_H,
            "category.kronos"
        ));
        
        this.keyPause = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.pause",
            GLFW.GLFW_KEY_J,
            "category.kronos"
        ));
        
        this.keyResume = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.resume",
            GLFW.GLFW_KEY_K,
            "category.kronos"
        ));
        
        this.keyEmergencyStop = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.emergency_stop",
            GLFW.GLFW_KEY_DELETE,
            "category.kronos"
        ));
        
        this.keyGuiOpen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.gui_open",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.kronos"
        ));
        
        this.keyHudToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.hud_toggle",
            GLFW.GLFW_KEY_U,
            "category.kronos"
        ));
        
        this.keyMiningToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.mining_toggle",
            GLFW.GLFW_KEY_I,
            "category.kronos"
        ));
        
        this.keyBuilderToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.builder_toggle",
            GLFW.GLFW_KEY_O,
            "category.kronos"
        ));
        
        this.keyLegitToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.kronos.legit_toggle",
            GLFW.GLFW_KEY_P,
            "category.kronos"
        ));
    }
    
    public boolean isStartPressed() {
        boolean isPressed = keyStart.isPressed();
        if (isPressed && !lastStartState) {
            lastStartState = true;
            return true;
        }
        lastStartState = isPressed;
        return false;
    }
    
    public boolean isPausePressed() {
        boolean isPressed = keyPause.isPressed();
        if (isPressed && !lastPauseState) {
            lastPauseState = true;
            return true;
        }
        lastPauseState = isPressed;
        return false;
    }
    
    public boolean isResumePressed() {
        boolean isPressed = keyResume.isPressed();
        if (isPressed && !lastResumeState) {
            lastResumeState = true;
            return true;
        }
        lastResumeState = isPressed;
        return false;
    }
    
    public boolean isEmergencyStopPressed() {
        boolean isPressed = keyEmergencyStop.isPressed();
        if (isPressed && !lastEmergencyStopState) {
            lastEmergencyStopState = true;
            return true;
        }
        lastEmergencyStopState = isPressed;
        return false;
    }
    
    public boolean isGuiOpenPressed() {
        boolean isPressed = keyGuiOpen.isPressed();
        if (isPressed && !lastGuiState) {
            lastGuiState = true;
            return true;
        }
        lastGuiState = isPressed;
        return false;
    }
    
    public boolean isHudTogglePressed() {
        boolean isPressed = keyHudToggle.isPressed();
        if (isPressed && !lastHudState) {
            lastHudState = true;
            return true;
        }
        lastHudState = isPressed;
        return false;
    }
    
    public boolean isMiningTogglePressed() {
        return keyMiningToggle.isPressed();
    }
    
    public boolean isBuilderTogglePressed() {
        return keyBuilderToggle.isPressed();
    }
    
    public boolean isLegitTogglePressed() {
        return keyLegitToggle.isPressed();
    }
}
