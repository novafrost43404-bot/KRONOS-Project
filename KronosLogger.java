package net.kronos;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KronosLogger {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KronosMod.MOD_ID);
    
    public static void info(String message) {
        LOGGER.info(message);
        sendChat("§b[KRONOS]§r " + message);
    }
    
    public static void warn(String message) {
        LOGGER.warn(message);
        sendChat("§e[KRONOS]§r " + message);
    }
    
    public static void error(String message) {
        LOGGER.error(message);
        sendChat("§c[KRONOS]§r " + message);
    }
    
    public static void success(String message) {
        LOGGER.info(message);
        sendChat("§a[KRONOS]§r " + message);
    }
    
    public static void debug(String message) {
        LOGGER.debug(message);
    }
    
    private static void sendChat(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud != null) {
            client.inGameHud.addChatMessage(false, Text.of(message));
        }
    }
}
