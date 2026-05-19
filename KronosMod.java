package net.kronos;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.kronos.automation.task.TaskManager;
import net.kronos.automation.command.CommandManager;
import net.kronos.client.config.ConfigManager;
import net.kronos.client.gui.ClickGUI;
import net.kronos.client.input.KeybindManager;
import net.kronos.client.render.HudRenderer;
import net.kronos.client.render.RenderEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KronosMod implements ClientModInitializer {
    
    public static final String MOD_ID = "kronos";
    public static final String MOD_NAME = "KRONOS";
    public static final String VERSION = "1.0.0";
    
    private static KronosMod instance;
    
    private ConfigManager configManager;
    private KeybindManager keybindManager;
    private TaskManager taskManager;
    private RenderEngine renderEngine;
    private HudRenderer hudRenderer;
    private ClickGUI clickGUI;
    private CommandManager commandManager;
    
    private boolean isEnabled = false;
    private boolean isPaused = false;
    
    public KronosMod() {
        instance = this;
    }
    
    @Override
    public void onInitializeClient() {
        // Initialize config system
        this.configManager = new ConfigManager();
        this.configManager.loadConfig();
        
        // Initialize keybind system
        this.keybindManager = new KeybindManager();
        this.keybindManager.registerKeybinds();
        
        // Initialize core systems
        this.taskManager = new TaskManager();
        this.renderEngine = new RenderEngine();
        this.hudRenderer = new HudRenderer();
        this.clickGUI = new ClickGUI();
        this.commandManager = new CommandManager();
        
        // Register event handlers
        registerEventHandlers();
        
        KronosLogger.info("KRONOS v" + VERSION + " initialized successfully");
    }
    
    private void registerEventHandlers() {
        // Client tick event - main game loop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            onClientTick(client);
        });
        
        // HUD render event
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (isEnabled && clickGUI != null) {
                hudRenderer.render(matrixStack, tickDelta);
            }
        });
    }
    
    private void onClientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }
        
        // Handle keybinds
        handleKeybinds(client);
        
        // Process automation tasks
        if (isEnabled && !isPaused) {
            taskManager.tick();
        }
    }
    
    private void handleKeybinds(MinecraftClient client) {
        // Start automation
        if (keybindManager.isStartPressed()) {
            startAutomation();
        }
        
        // Pause automation
        if (keybindManager.isPausePressed()) {
            pauseAutomation();
        }
        
        // Resume automation
        if (keybindManager.isResumePressed()) {
            resumeAutomation();
        }
        
        // Emergency stop
        if (keybindManager.isEmergencyStopPressed()) {
            stopAutomation();
        }
        
        // Open GUI
        if (keybindManager.isGuiOpenPressed()) {
            clickGUI.toggle();
        }
        
        // Toggle HUD
        if (keybindManager.isHudTogglePressed()) {
            hudRenderer.toggle();
        }
    }
    
    public void startAutomation() {
        if (!isEnabled) {
            isEnabled = true;
            isPaused = false;
            taskManager.start();
            KronosLogger.info("§aAutomation started");
        }
    }
    
    public void pauseAutomation() {
        if (isEnabled && !isPaused) {
            isPaused = true;
            KronosLogger.info("§eAutomation paused");
        }
    }
    
    public void resumeAutomation() {
        if (isEnabled && isPaused) {
            isPaused = false;
            KronosLogger.info("§aAutomation resumed");
        }
    }
    
    public void stopAutomation() {
        if (isEnabled) {
            isEnabled = false;
            isPaused = false;
            taskManager.stop();
            renderEngine.clear();
            KronosLogger.info("§cAutomation stopped");
        }
    }
    
    // Getters
    public static KronosMod getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public KeybindManager getKeybindManager() {
        return keybindManager;
    }
    
    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    public RenderEngine getRenderEngine() {
        return renderEngine;
    }
    
    public HudRenderer getHudRenderer() {
        return hudRenderer;
    }
    
    public ClickGUI getClickGUI() {
        return clickGUI;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public boolean isAutomationEnabled() {
        return isEnabled;
    }
    
    public boolean isAutomationPaused() {
        return isPaused;
    }
}
