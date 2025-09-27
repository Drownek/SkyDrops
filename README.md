<div align="center">
  

# SkyDrops

[](https://www.spigotmc.org/resources/skydrops.129111/)

A custom airdrops for your Minecraft server!

<img src="https://i.imgur.com/Ncs73SN.png" alt="Airdrop landing with a particle effect" />

</div>


## Installation & Prerequisites

**Prerequisites:**

  * Java 17 or higher
  * **[DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-21-8-papi-support-no-dependencies.96927/)** (Required for all hologram functionality)

**Quick Start:**

1.  Download the **SkyDrops JAR** from [SpigotMC](https://www.spigotmc.org/resources/skydrops.129111/).
2.  Place the JAR into your server's `plugins/` directory.
3.  Restart the server.
4.  Configure global settings via `/skydrops settings`.
5.  Define airdrop contents via `/skydrops edit-drops`.


## Core Functionality

### In-game Configuration

SkyDrops features a complete **GUI-based settings editor**, eliminating the need for manual file editing. All core parameters, including drop announcement times and scheduled intervals, are configurable in real-time.

<div align="center">
<img width="553" height="435" alt="In-game Settings Editor GUI showing 'Time before drop to announce'" src="https://i.imgur.com/C61vXmW.png" />
</div>

### Drop Claim Mechanics

To secure an airdrop's contents, players must interact with the drop chest a pre-configured number of times. This introduces a controlled delay to the claim process.

<div align="center">
<img src="https://i.imgur.com/amHNphY.png" alt="Airdrop chest with a 'CLICK RIGHT' prompt and click counter" />
</div>


## Commands

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/skydrops settings` | `skydrops.ingame.settings` | Opens the GUI for global plugin configuration. |
| `/skydrops edit-drops` | `skydrops.edit.drops` | Opens the GUI for modifying airdrop loot tables. |
| `/skydrops reload` | `skydrops.command.reload` | Reloads all plugin configurations (non-drop-related). |
| `/skydrops removedrops` | `skydrops.command.removedrops` | Removes all active, unclaimed airdrops from the world. |
| `/skydrops spawn-drop <location>` | `skydrops.command.spawndrop` | Manually initiates an airdrop at the specified coordinate. |
| `/skydrops set-next-location <location>` | `skydrops.command.setnextlocation` | Overrides the next scheduled airdrop location. |


## Permissions

| Permission | Description |
| :--- | :--- |
| `skydrops.command.editdrops` | Grants access to the loot table editing GUI. |
| `skydrops.command.reload` | Grants permission to reload configurations. |
| `skydrops.command.removedrops` | Grants permission to forcefully remove active airdrops. |
| `skydrops.command.spawndrop` | Grants permission for manual airdrop spawning. |
| `skydrops.command.setnextlocation` | Grants permission to pre-define the next airdrop location. |
| `skydrops.command.settings` | Grants access to the in-game settings GUI. |


## Technical Dependencies

SkyDrops leverages the following external libraries for enhanced performance and features:

  * [light-platform](https://github.com/Drownek/light-platform)
  * [bukkit-utils](https://github.com/Drownek/bukkit-utils)
  * [LiteCommands](https://github.com/Rollczi/LiteCommands)
  * [TriumphGui](https://github.com/TriumphTeam/triumph-gui)
  * [SignGUI](https://github.com/Rapha149/SignGUI)


## Tested Minecraft Versions

The plugin was tested on following paper versions:
  * 1.18.2
  * 1.19.4
  * 1.20.6
  * 1.21.5
  * 1.21.6
  * 1.21.7
  * 1.21.8

For support or bug reporting, please reach out on Discord: **`drownek`** or **open an issue** on GitHub.


## 📜 License

Project is licensed under [MIT](https://choosealicense.com/licenses/mit/).

This means that...

- ✅ You can freely use, copy, modify, and distribute this project, even for commercial purposes.
- 🧾 You **must include the original license and copyright notice** in any copies or substantial portions.
- ❌ The software is provided **"as is"**, without warranty of any kind. The author is **not liable** for any damages or issues caused by using it.
