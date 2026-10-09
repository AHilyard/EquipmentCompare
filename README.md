# Equipment Compare (26.3 Fabric port)

Unofficial port of [Equipment Compare](https://github.com/AHilyard/EquipmentCompare) to Minecraft **26.3** (Fabric).

## Dependencies (required)

| Mod | Version |
|-----|---------|
| **Iceberg** | **1.4.3+** for 26.3 (e.g. `Iceberg-26.3-fabric-1.4.3.1`) |
| Fabric API | any 26.3 |
| Fabric Loader | ≥ 0.19 |

Iceberg is **not** bundled. Put both jars in your `mods/` folder.

## Build

```bash
# JDK 25
./gradlew build
```

Iceberg is resolved from Modrinth Maven at compile time (`modImplementation`).

## Usage

Hold **Left Shift** (keybind configurable) while hovering equipment to show comparison tooltips.

## Credits

Original author: Grend / Anthony Hilyard
