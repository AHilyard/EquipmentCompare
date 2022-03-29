# Changelog

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