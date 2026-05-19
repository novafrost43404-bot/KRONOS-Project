# KRONOS-Project

An advanced AI-powered automation and utility mod built for the Minecraft Fabric toolchain. Designed to streamline building, mining, survival automation, inventory optimization, schematic construction, and intelligent world interaction systems.

---

## Features

### 🏗 Building Systems

* **Schematic Builder:** Automatically builds structures from `.schem`, `.schematic`, and `.nbt` files.
* **PlacementEngine:** Advanced block placement engine with rotation handling, scaffold support, and placement verification.
* **Ghost Rendering:** Real-time hologram preview rendering for schematics and planned structures.
* **Material Scanner:** Scans inventory and validates required resources before construction starts.
* **Large Build Support:** Optimized for massive structures and chunk-based building operations.

### ⛏ Mining Systems

* **MiningEngine:** Intelligent mining AI capable of strip mining, branch mining, tunnel mining, and quarry automation.
* **OreScanner:** Advanced ore detection and targeting system for diamonds, emeralds, ancient debris, and more.
* **Auto Tool Switching:** Automatically selects the best tool for mining and interaction tasks.
* **Pathfinding Miner:** Navigates terrain and safely mines targeted ores while avoiding hazards.

### ❤️ Survival Automation

* **AutoTotem:** Instant offhand totem replacement for emergency survival situations.
* **AutoEat & AutoHeal:** Automatically consumes food or healing items based on health conditions.
* **Lava & Fall Protection:** Emergency safety systems for dangerous terrain and combat scenarios.
* **Smart Escape Logic:** Detects nearby threats and reacts dynamically.

### 🎒 Inventory & Utility Systems

* **Inventory Management:** Smart inventory sorting, junk cleanup, and automatic hotbar refueling.
* **Storage Automation:** Supports chest depositing, item withdrawing, and storage organization.
* **Auto Armor:** Automatically equips the strongest available armor.
* **Macro System:** Supports configurable task sequences and automation routines.

### 🧠 AI & Pathfinding

* **A* Pathfinding Engine:** Advanced navigation system with obstacle avoidance and dynamic rerouting.
* **Task Scheduler:** Queue-based AI task manager for automation workflows.
* **Movement AI:** Humanized rotations, movement smoothing, and legit-mode simulation systems.

### 👁 Render & HUD Systems

* **ESP Rendering:** Ore ESP, block ESP, and placement visualization systems.
* **Custom HUD:** Draggable HUD with live statistics, module status, and task information.
* **Chunk Border Rendering:** Visual chunk display for large-scale automation operations.

---

## Default Keybinds

| Key           | Action            |
| ------------- | ----------------- |
| `H`           | Start Automation  |
| `J`           | Pause Automation  |
| `K`           | Resume Automation |
| `DELETE`      | Emergency Stop    |
| `RIGHT_SHIFT` | Open KRONOS GUI   |
| `I`           | Toggle Mining AI  |
| `O`           | Toggle Builder AI |
| `P`           | Toggle Legit Mode |

---

## Commands

```bash
.kronos start
.kronos stop
.kronos pause
.kronos resume
.kronos mine
.kronos build
.kronos scan
.kronos status
.kronos schematic <name>
```

---

## Installation

1. Make sure you have the correct version of **Fabric Loader** installed.
2. Install the required version of **Fabric API**.
3. Drop the compiled `.jar` file into your `.minecraft/mods` folder.
4. Launch Minecraft using the Fabric profile.
5. Open the KRONOS GUI using `RIGHT_SHIFT`.

---

## Directory Structure

```plaintext
.minecraft/config/kronos/
```

### Includes:

```plaintext
config.json
schematics/
macros/
profiles/
logs/
```

---

## Performance Notes

KRONOS is optimized for:

* Large schematic processing
* Chunk-based rendering
* Multi-threaded automation
* Efficient pathfinding
* FPS-conscious rendering systems

Recommended:

* Java 17+
* 8GB+ RAM
* SSD Storage
* Dedicated GPU

---

## Disclaimer

KRONOS is intended for educational, development, and private testing purposes only.

The developers and contributors of KRONOS are **NOT responsible** for:

* Server bans
* Anti-cheat detections
* Account suspensions
* Data loss
* Corrupted worlds
* Crashes
* Third-party conflicts
* Misuse of the software

Using automation software on multiplayer servers may violate server rules or terms of service.

### USE AT YOUR OWN RISK.

---

## Developer Notice

KRONOS uses:

* Fabric API
* Fabric Loom
* Java 17
* Yarn mappings
* Multi-threaded task systems

If you are compiling from source or running locally:

```bash
./gradlew build
```

Make sure:

* `JAVA_HOME` points to a valid Java 17 installation.
* Fabric Loom dependencies are installed correctly.
* Gradle dependencies are refreshed before compiling.

---

# KRONOS

### Advanced Minecraft Automation & AI Utility Framework

