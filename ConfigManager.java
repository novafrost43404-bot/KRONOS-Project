package net.kronos.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.kronos.KronosLogger;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {
    
    private static final String CONFIG_DIR = "config/kronos";
    private static final String CONFIG_FILE = "config.json";
    private static final String SCHEMATICS_DIR = "schematics";
    
    private JsonObject config;
    private File configPath;
    private Gson gson;
    
    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        initializeDirectories();
        this.config = new JsonObject();
    }
    
    private void initializeDirectories() {
        try {
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            
            File schematicsDir = new File(CONFIG_DIR + "/" + SCHEMATICS_DIR);
            if (!schematicsDir.exists()) {
                schematicsDir.mkdirs();
            }
            
            this.configPath = new File(CONFIG_DIR, CONFIG_FILE);
            if (!configPath.exists()) {
                createDefaultConfig();
            }
        } catch (Exception e) {
            KronosLogger.error("Failed to initialize config directories: " + e.getMessage());
        }
    }
    
    private void createDefaultConfig() {
        try {
            JsonObject defaultConfig = new JsonObject();
            
            // Keybind settings
            JsonObject keybinds = new JsonObject();
            keybinds.addProperty("start", 72); // H
            keybinds.addProperty("pause", 74); // J
            keybinds.addProperty("resume", 75); // K
            keybinds.addProperty("emergency_stop", 261); // DELETE
            keybinds.addProperty("gui_open", 340); // RIGHT_SHIFT
            keybinds.addProperty("hud_toggle", 85); // U
            keybinds.addProperty("mining_toggle", 73); // I
            keybinds.addProperty("builder_toggle", 79); // O
            keybinds.addProperty("legit_toggle", 80); // P
            defaultConfig.add("keybinds", keybinds);
            
            // Builder settings
            JsonObject builder = new JsonObject();
            builder.addProperty("enabled", false);
            builder.addProperty("speed", 10);
            builder.addProperty("legit_mode", false);
            builder.addProperty("rotation_smoothing", 0.5f);
            builder.addProperty("placement_delay", 50);
            builder.addProperty("verify_placements", true);
            defaultConfig.add("builder", builder);
            
            // Miner settings
            JsonObject miner = new JsonObject();
            miner.addProperty("enabled", false);
            miner.addProperty("speed", 8);
            miner.addProperty("mine_diamonds_only", false);
            miner.addProperty("auto_tool_selection", true);
            miner.addProperty("torch_placement", true);
            defaultConfig.add("miner", miner);
            
            // AutoTotem settings
            JsonObject totem = new JsonObject();
            totem.addProperty("enabled", true);
            totem.addProperty("health_threshold", 7);
            totem.addProperty("legit_mode", true);
            defaultConfig.add("auto_totem", totem);
            
            // HUD settings
            JsonObject hud = new JsonObject();
            hud.addProperty("enabled", true);
            hud.addProperty("x", 10);
            hud.addProperty("y", 10);
            hud.addProperty("scale", 1.0f);
            defaultConfig.add("hud", hud);
            
            // Render settings
            JsonObject render = new JsonObject();
            render.addProperty("render_distance", 128);
            render.addProperty("ghost_blocks_enabled", true);
            render.addProperty("esp_enabled", false);
            render.addProperty("transparency", 0.5f);
            defaultConfig.add("render", render);
            
            // General settings
            defaultConfig.addProperty("version", "1.0.0");
            
            config = defaultConfig;
            saveConfig();
            KronosLogger.success("Default config created");
        } catch (Exception e) {
            KronosLogger.error("Failed to create default config: " + e.getMessage());
        }
    }
    
    public void loadConfig() {
        try {
            if (configPath.exists()) {
                FileReader reader = new FileReader(configPath);
                config = gson.fromJson(reader, JsonObject.class);
                reader.close();
                KronosLogger.success("Config loaded");
            } else {
                createDefaultConfig();
            }
        } catch (IOException e) {
            KronosLogger.error("Failed to load config: " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    public void saveConfig() {
        try {
            FileWriter writer = new FileWriter(configPath);
            gson.toJson(config, writer);
            writer.close();
            KronosLogger.success("Config saved");
        } catch (IOException e) {
            KronosLogger.error("Failed to save config: " + e.getMessage());
        }
    }
    
    public void set(String key, Object value) {
        if (value instanceof String) {
            config.addProperty(key, (String) value);
        } else if (value instanceof Number) {
            config.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
            config.addProperty(key, (Boolean) value);
        }
        saveConfig();
    }
    
    public String getString(String key, String defaultValue) {
        try {
            return config.get(key).getAsString();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public int getInt(String key, int defaultValue) {
        try {
            return config.get(key).getAsInt();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public float getFloat(String key, float defaultValue) {
        try {
            return config.get(key).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return config.get(key).getAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public JsonObject getSection(String section) {
        try {
            return config.getAsJsonObject(section);
        } catch (Exception e) {
            return new JsonObject();
        }
    }
    
    public File getConfigDir() {
        return new File(CONFIG_DIR);
    }
    
    public File getSchematicsDir() {
        return new File(CONFIG_DIR + "/" + SCHEMATICS_DIR);
    }
}
