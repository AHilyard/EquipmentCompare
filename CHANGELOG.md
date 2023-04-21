# Changelog

### 1.3.6
- Added French localization.
- Added item selector support to blacklist entries.

### 1.3.5
- Added a "maximum_comparisons" configuration option to limit the number of comparison tooltips onscreen at once.  Defaults to 3.
- Fixed a tooltip alignment bug when multiple comparisons were active (such as with Curios).
- Added Ukrainian localization.
- Added Russian localization.
- First Minecraft 1.19.4 release.

### 1.3.4
- Fixed intermittent crash when comparing items.
- Fixed visual glitch in inventory caused by conflicts with some mods.
- Fixed upper part of comparison tooltip being miscolored when "borders_match_rarity" is turned off and Legendary Tooltips is installed.
- Fixed two misplaced pixels in comparison tooltips.
- Added Portuguese localization.

### 1.3.3
- Fixed a bug that prevented certain modded tools from being compared.

### 1.3.2
- Added support for Minecraft 1.19.3.
- Fixed a bug that caused the keybind to be missing from the controls screen.
- Fixed a bug that caused the comparison tooltips key to stick under certain circumstances.

### 1.3.1
- Fixed an alignment bug that could happen when using translated badge text.
- First version for Fabric 1.19.

### 1.3.0
- Improved compatibility with other mods.  REI and similar are now supported for comparisons.
- You can now compare from anvils, crafting tables, and most other menus.
- Badge text and keybinding name are now translatable strings.
- Added a configuration option for overriding the translatable badge text. (This defaults to false.)
- Changed invalid update check url. (Forge only)
- First version for Forge 1.19.

### 1.2.12
- Fixed an incompatibility with newer versions of Trinkets that was causing log spam and a memory leak.

### 1.2.11
- Fixed a bug where the equipped badge's background color did not match custom Legendary Tooltips background colors.
- Updated mod icon.
- Improved support for new 1.18 tooltip components.
- Fixed a server crash when attempting to install Equipment Compare on a dedicated server.

### 1.2.10
- Added support for Legendary Tooltips 1.2 features.
- Fixed a bug where comparison tooltips sometimes rendered improperly.

### 1.2.9
- Fixed an issue with very short-named items in conjunction with Legendary Tooltips.
- All items with durability can now be compared, including bows, crossbows, tridents, fishing poles, shears, etc.
- First Forge 1.18 release.
- First Fabric 1.18 release.

### 1.2.8
- Fixed an issue where the comparison tooltip key bind would overwrite other key binds that shared a key.

### 1.2.7
- Updated to support latest baubles API.
- Fixed an alignment issue for some items when used with Legendary Tooltips.
- First Fabric 1.17 release.

### 1.2.6
- Fixed an issue that was causing parts of the tooltip border to be misaligned when Legendary Tooltips was installed.

### 1.2.5
- Fixed an issue where conflicting keybinds could sometimes cause issues with other mods.
- Fixed an issue where the "equipped" badge's border would not longer draw when Legendary Tooltips was not installed.
- First Forge 1.17 release.

### 1.2.4
- Fixed background color discrepancy when used in conjunction with Legendary Tooltips.

### 1.2.3
- Added option to change equipped badge text color.
- Added integration support for Legendary Tooltips.

### 1.2.2
- Fixed a bug that was causing duplicate tooltips when trying to compare currently-equipped Curios.
- Fixed a bug that was causing tooltips to sometimes overlap with eachother.

### 1.2.1
- Added a blacklist configuration option to prevent certain items from being compared.

### 1.2.0
- Added support for Baubles, which should work identically to Curios.

### 1.1.1
- Fixed a bug that prevented swords from being compared.

### 1.1.0
- When comparing held items, only tools will be comparable now.
- Added a new "strict" configuration option to only compare tools of the same types. (If you don't want to be able to compare swords to axes, for example)
- Added Curios support--you can even compare multiple items at once if there are more than one slot of a single type.

### 1.0.1
- Mainhand equipment can now be compared (tools and weapons).

### 1.0.0
- Initial Forge 1.16 release.