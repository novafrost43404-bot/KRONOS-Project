package net.kronos.automation.task;

import net.kronos.KronosLogger;
import net.kronos.automation.builder.PlacementEngine;
import net.kronos.automation.miner.MiningEngine;
import net.kronos.automation.miner.OreScanner;
import net.kronos.automation.survival.AutoTotem;
import net.kronos.automation.inventory.InventoryManager;
import net.minecraft.client.MinecraftClient;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskManager {
    
    private enum AutomationState {
        IDLE, BUILDING, MINING, FARMING, TRAVELING
    }
    
    private final Queue<AutomationTask> taskQueue;
    private AutomationTask currentTask;
    private AutomationState state;
    
    private PlacementEngine placementEngine;
    private MiningEngine miningEngine;
    private OreScanner oreScanner;
    private AutoTotem autoTotem;
    private InventoryManager inventoryManager;
    
    private long lastTaskSwitch;
    private boolean isRunning;
    
    public TaskManager() {
        this.taskQueue = new ConcurrentLinkedQueue<>();
        this.state = AutomationState.IDLE;
        this.lastTaskSwitch = System.currentTimeMillis();
        
        // Initialize automation systems
        this.placementEngine = new PlacementEngine();
        this.miningEngine = new MiningEngine();
        this.oreScanner = new OreScanner();
        this.autoTotem = new AutoTotem();
        this.inventoryManager = new InventoryManager();
    }
    
    public void start() {
        this.isRunning = true;
        state = AutomationState.MINING; // Default to mining
        KronosLogger.info("Task manager started");
    }
    
    public void stop() {
        this.isRunning = false;
        taskQueue.clear();
        currentTask = null;
        state = AutomationState.IDLE;
        
        // Stop all active systems
        if (miningEngine != null) {
            miningEngine.stop();
        }
        if (placementEngine != null) {
            placementEngine.stop();
        }
        
        KronosLogger.info("Task manager stopped");
    }
    
    public void tick() {
        if (!isRunning || MinecraftClient.getInstance().player == null) {
            return;
        }
        
        // Always run AutoTotem (survival priority)
        if (autoTotem != null) {
            autoTotem.tick();
        }
        
        // Process current task
        processCurrentTask();
        
        // Handle task switching
        if (currentTask == null || currentTask.isComplete()) {
            switchTask();
        }
    }
    
    private void processCurrentTask() {
        if (currentTask == null) {
            return;
        }
        
        try {
            currentTask.execute();
        } catch (Exception e) {
            KronosLogger.error("Task execution error: " + e.getMessage());
            currentTask.cancel();
            currentTask = null;
        }
    }
    
    private void switchTask() {
        if (!taskQueue.isEmpty()) {
            currentTask = taskQueue.poll();
            state = determineState(currentTask);
            lastTaskSwitch = System.currentTimeMillis();
        } else {
            // Auto-generate mining task
            currentTask = new MiningTask(miningEngine, oreScanner);
            state = AutomationState.MINING;
        }
    }
    
    private AutomationState determineState(AutomationTask task) {
        if (task instanceof MiningTask) {
            return AutomationState.MINING;
        } else if (task instanceof BuildingTask) {
            return AutomationState.BUILDING;
        }
        return AutomationState.IDLE;
    }
    
    public void queueTask(AutomationTask task) {
        taskQueue.add(task);
    }
    
    public AutomationState getState() {
        return state;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public PlacementEngine getPlacementEngine() {
        return placementEngine;
    }
    
    public MiningEngine getMiningEngine() {
        return miningEngine;
    }
    
    public AutoTotem getAutoTotem() {
        return autoTotem;
    }
    
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    
    // Task base class
    public abstract static class AutomationTask {
        protected boolean complete = false;
        
        public abstract void execute();
        
        public void cancel() {
            complete = true;
        }
        
        public boolean isComplete() {
            return complete;
        }
    }
    
    // Mining task
    public static class MiningTask extends AutomationTask {
        private MiningEngine miningEngine;
        private OreScanner oreScanner;
        private long startTime;
        
        public MiningTask(MiningEngine engine, OreScanner scanner) {
            this.miningEngine = engine;
            this.oreScanner = scanner;
            this.startTime = System.currentTimeMillis();
        }
        
        @Override
        public void execute() {
            if (miningEngine == null) {
                complete = true;
                return;
            }
            
            miningEngine.tick();
            
            // Timeout after 5 minutes of no ore found
            if (System.currentTimeMillis() - startTime > 300000) {
                complete = true;
            }
        }
    }
    
    // Building task
    public static class BuildingTask extends AutomationTask {
        private PlacementEngine placementEngine;
        
        public BuildingTask(PlacementEngine engine) {
            this.placementEngine = engine;
        }
        
        @Override
        public void execute() {
            if (placementEngine == null) {
                complete = true;
                return;
            }
            
            placementEngine.tick();
            
            if (placementEngine.isComplete()) {
                complete = true;
            }
        }
    }
}
