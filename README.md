# RoadCraft (Minecraft 1.19.2, Forge 43.x)

Roads, traffic lights, a gas station and fully editable city / highway signs.

## What's in it

| Thing | What it does |
|---|---|
| **Asphalt**, **Asphalt Slab** | Road surface. Walking on it is 15% faster. |
| **Road lines** (white, double yellow), **Crosswalk** | Painted asphalt. The line runs in the direction you were looking when you placed it. |
| **Concrete Barrier** | Highway barrier, also runs along your look direction. |
| **Traffic Light** | Cycles red 10 s, green 8 s, yellow 2 s. Right-click to advance it (use this to synchronise lights). Double sided. |
| **Sign Pole** | Stack these; the sign plate sits right in front of a pole. |
| **Road Sign** | Right-click to edit (see below). |
| **Gas Pump**, **Gas Station Canopy**, **Fuel Can** | Right-click the pump with an empty Fuel Can + 1 emerald to fill it. A full can burns 6400 ticks in a furnace and gives the empty can back. |

The creative tab "RoadCraft" also has ready-made signs: Highway, Highway Exit, City Direction, City Limit, Gas Station.

## Editing a sign

Right-click a Road Sign. Close the screen (Done or Esc) to save.

- **4 rows**, each with its own text (up to 32 chars) and arrow: none, up, up-right, right, down-right, down, down-left, left, up-left. Left-pointing arrows are drawn on the left of the row, the rest on the right.
- **Style**: Highway (green), City (blue), City limit (white), Tourist (brown), Warning (yellow), Gas station.
- **Font**: cycles through the fonts in `RoadSignBlockEntity.FONTS`.
- **Width** (1-8 blocks), **Height** (1-4 blocks): the panel is centred on the block and drawn bigger than the block itself.
- **Text size**: 30-100% of the biggest size that fits the row.

Text is always shrunk to fit its row, so long names never overflow.

Note: a wide sign is only clickable/solid at its own 1x1 block (the extra width is visual).

## Adding a custom font

1. Put your font file at `src/main/resources/assets/roadcraft/font/highway.ttf`.
2. Create `src/main/resources/assets/roadcraft/font/highway.json`:
   ```json
   { "providers": [ { "type": "ttf", "file": "roadcraft:highway.ttf", "shift": [0, 0], "size": 11.0, "oversample": 4.0 } ] }
   ```
3. Add `"roadcraft:highway"` to the end of `FONTS` in `RoadSignBlockEntity.java`.

## Building

You need JDK 17.

Easiest way: download the official **Forge 1.19.2 MDK** (43.x) from files.minecraftforge.net, then copy this project's `src/`, `build.gradle`, `gradle.properties` and `settings.gradle` over the MDK's. Then:

```
./gradlew build          # jar ends up in build/libs/
./gradlew runClient      # test in-game
```

This project's `build.gradle` follows the 1.19.2 MDK (ForgeGradle 5.1, Gradle 7.x). The Gradle wrapper jar/scripts are not included, so use the ones from the MDK.

## Layout

```
src/main/java/com/roadcraft/
  block/        all blocks (traffic light logic, gas pump logic, sign block)
  blockentity/  RoadSignBlockEntity  (stores text, arrows, font, style, size)
  client/       RoadSignRenderer (draws the sign), RoadSignEditScreen (the editor)
  network/      packet that sends edits from the editor to the server
  sign/         arrow + style enums, creative-tab presets
src/main/resources/  models, blockstates, textures (generated), lang, recipes, loot tables
```
