# JxCustomRPGTitles

A custom Minecraft Forge mod for **1.20.1**, designed for RPG maps and adventure systems. It features programmable blocks that execute commands with logic, player interaction, and redstone input. Includes dynamic player input, teleportation (with pet support), villager trading, chest generation, and more.

---

## 🔧 Features

- **Programmable Command Blocks**
  - Triggered by redstone or proximity
  - Optional creative-only or single-use settings
  - Unlimited commands in one block for example
- **Teleport with Pets**
  - Teleports player and his unseated tamed pets with them — even across dimensions
- **Villager Traders**
  - Spawn custom villagers that sell modded items
- **Chest & Item Manipulation**
  - Spawn chests, add custom items using CustomModelData
- **Safe Teleport Handling**
  - Redstone-activated teleport avoids instant re-trigger from pressure plates

---

## 🧪 Example Commands

### 🚪 Teleport Player to Another Dimension (with Pets)

> ⚠️ Must be triggered via **Redstone**, NOT proximity, or it may loop endlessly.

```mcfunction
/execute in minecraft:overworld run tp @p 793 92 1903
```
> Teleports player to Overworld at (793, 92, 1903) and brings nearby tamed pets.

---

### 🧙‍♂️ Spawn Villager Merchant (e.g. "Griphook")

```mcfunction
/execute positioned %d %d %d unless entity @e[type=minecraft:villager, name="Griphook", distance=..5] run summon minecraft:villager ~ ~ ~ {
  CustomName:'{"text":"Griphook"}',
  NoAI:1b,
  Invulnerable:1b,
  Silent:1b,
  Rotation:[%d.0f,0.0f],
  VillagerData:{profession:toolsmith,level:2,type:plains},
  Offers:{Recipes:[
    {buy:{id:"minecraft:paper",Count:1,tag:{display:{Name:'{"text":"@p","color":"gold","italic":false}',Lore:['{"text":"Personal ID","color":"dark_purple","italic":false}']}},sell:{id:"hogcraft:galleon",Count:20},maxUses:9999999,rewardExp:false},
    {buy:{id:"minecraft:gold_block",Count:1},sell:{id:"hogcraft:galleon",Count:1},maxUses:9999999,rewardExp:false}
  ]}
}
```

---

### 🖥️ Display RPG Title to Player

```mcfunction
/title @p times 10 70 20
/title @p title {"text":"Gryffindor!","color":"red","bold":true}
/playsound minecraft:entity.experience_orb.pickup master @p ~ ~ ~ 1 1
```

---

### 📦 Spawn Chest and Add Custom Paper

```mcfunction
/execute positioned %d %d %d unless block ~ ~ ~ minecraft:chest run setblock ~ ~ ~ minecraft:chest
/execute positioned %d %d %d run data modify block ~ ~ ~ Items append value {
  id:"minecraft:paper",Count:1b,
  tag:{CustomModelData:199999,display:{Name:'{"text":"@p","color":"gold","italic":false}',Lore:['{"text":"Personal ID","color":"dark_purple","italic":false}']}}
}
```

---

### 🌳 Build Tree with Setblock Commands

```mcfunction
/execute positioned %d %d %d run setblock ~ ~1 ~ hexerei:willow_log
/execute positioned %d %d %d run setblock ~ ~2 ~ hexerei:willow_log
/execute positioned %d %d %d run setblock ~ ~3 ~ hexerei:willow_log
/execute positioned %d %d %d run setblock ~ ~4 ~ hexerei:willow_leaves
```

---

## 🧰 Installation

1. Requires Minecraft **Forge 1.20.1**
2. Place the `.jar` into your `mods/` folder
3. Launch the game


## note: if you ever put a proximity trigger on a teleport block and cannot approach the block to change it use this command: /crpgblocks_disable

---

## 📝 License

MIT License — free to use, modify, and distribute.

---

### 💬 Feedback & Issues

Please open issues or suggestions on [GitHub](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME) if you'd like to contribute or report bugs.

---

*Create immersive RPG experiences in Minecraft with logic-based command blocks and storytelling tools.*
