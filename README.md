# SkyDrops
[![SpigotMC](https://img.shields.io/badge/Available_on-SpigotMC-orange)](https://www.spigotmc.org/resources/skydrops.129111/)

Plugin adding airdrops with modifiable loot and in-game configuration.

## Quick Start

1. Download the plugin from [SpigotMC](https://www.spigotmc.org/resources/skydrops.129111/)
2. Install [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-21-8-papi-support-no-dependencies.96927/) plugin if you haven't already (required for holograms)
3. Drop the JAR file into your server's `plugins` folder
4. Restart your server
5. Run `/skydrops settings` to configure the plugin
6. Use `/skydrops edit-drops` to customize your loot tables

## Commands

| Command                                  | Permission                         | Description                                                  |
|------------------------------------------|------------------------------------|--------------------------------------------------------------|
| `/skydrops settings`                     | `skydrops.ingame.settings`         | Open the GUI to configure all plugin settings                |
| `/skydrops edit-drops`                   | `skydrops.edit.drops`              | Open the GUI to modify airdrop loot                          |
| `/skydrops reload`                       | `skydrops.command.reload`          | Reload all plugin configurations                             |
| `/skydrops removedrops`                  | `skydrops.command.removedrops`     | Remove all active airdrops from the world                    |
| `/skydrops spawn-drop <location>`        | `skydrops.command.spawndrop`       | Manually spawn an airdrop at the specified location          |
| `/skydrops set-next-location <location>` | `skydrops.command.setnextlocation` | Set the location for the next scheduled airdrop              |

## Permissions

| Permission                         | Description                                              |
|------------------------------------|----------------------------------------------------------|
| `skydrops.command.editdrops`       | Allows editing airdrop loot through the GUI              |                                                          |
| `skydrops.command.reload`          | Allows reloading plugin configurations                   |
| `skydrops.command.removedrops`     | Allows removing all active airdrops from the world       |
| `skydrops.command.spawndrop`       | Allows manually spawning airdrops at specified locations |
| `skydrops.command.setnextlocation` | Allows setting the next airdrop location                 |
| `skydrops.command.settings`        | Allows opening in-game GUI settings                      |

## Libraries used

- [light-platform](https://github.com/Drownek/light-platform)
- [bukkit-utils](https://github.com/Drownek/bukkit-utils)
- [LiteCommands](https://github.com/Rollczi/LiteCommands)
- [TriumphGui](https://github.com/TriumphTeam/triumph-gui)
- [SignGUI](https://github.com/Rapha149/SignGUI)

## Supported versions
All servers must run under Java 17+ in order for the plugin to work.

Plugin was tested under the following versions:
- 1.18.2
- 1.19.4
- 1.20.6
- 1.21.5
- 1.21.6
- 1.21.7

If you have some issues, feel free to reach me on discord: `drownek` or create an issue in GitHub repository!

## 📜 License

Project is licensed under [MIT](https://choosealicense.com/licenses/mit/).

This means that...

- ✅ You can freely use, copy, modify, and distribute this project, even for commercial purposes.
- 🧾 You **must include the original license and copyright notice** in any copies or substantial portions.
- ❌ The software is provided **"as is"**, without warranty of any kind. The author is **not liable** for any damages or issues caused by using it.
