package com.anthonyhilyard.equipmentcompare;

import com.anthonyhilyard.equipmentcompare.config.EquipmentCompareConfig;
import com.anthonyhilyard.iceberg.services.Services;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EquipmentCompare
{
	public static final String MODID = "equipmentcompare";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static boolean comparisonsActive = false;

	/** Default: Left Shift — via named key so Controls shows a proper label. */
	public static KeyMapping showComparisonTooltip;

	public static void init()
	{
		InputConstants.Key leftShift = InputConstants.getKey("key.keyboard.left.shift");
		showComparisonTooltip = Services.getKeyMappingRegistrar().registerMapping(
			new KeyMapping("key.equipmentcompare.show_comparison", leftShift.getValue(), KeyMapping.Category.INVENTORY)
		);
		EquipmentCompareConfig.register(EquipmentCompareConfig.class, MODID);
	}
}
